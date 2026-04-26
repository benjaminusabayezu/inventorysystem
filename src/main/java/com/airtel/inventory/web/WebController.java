package com.airtel.inventory.web;

import com.airtel.inventory.dto.DeviceDTO;
import com.airtel.inventory.dto.TransactionDTO;
import com.airtel.inventory.dto.UserDTO;
import com.airtel.inventory.model.Device;
import com.airtel.inventory.model.Transaction;
import com.airtel.inventory.model.User;
import com.airtel.inventory.service.DeviceService;
import com.airtel.inventory.service.TransactionService;
import com.airtel.inventory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
public class WebController {

    private final DeviceService deviceService;
    private final UserService userService;
    private final TransactionService transactionService;

    @Autowired
    public WebController(DeviceService deviceService,
                         UserService userService,
                         TransactionService transactionService) {
        this.deviceService = deviceService;
        this.userService = userService;
        this.transactionService = transactionService;
    }

    // ── Root & Login ─────────────────────────────────────
    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String login(Authentication auth) {
        // If already logged in, skip login page
        if (auth != null && auth.isAuthenticated()) {
            return "redirect:/dashboard";
        }
        return "login";
    }

    // ── Dashboard ─────────────────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        try {
            Map<String, Long> stats = deviceService.getDashboardStats();
            List<TransactionDTO> recent = transactionService.getAllTransactions();
            if (recent.size() > 10) recent = recent.subList(0, 10);
            model.addAttribute("stats", stats);
            model.addAttribute("recentTransactions", recent);
        } catch (Exception e) {
            model.addAttribute("stats", Map.of("total",0L,"available",0L,"assigned",0L,"repair",0L,"lost",0L));
            model.addAttribute("recentTransactions", List.of());
        }
        model.addAttribute("activePage", "dashboard");
        return "dashboard";
    }

    // ── Devices ──────────────────────────────────────────
    @GetMapping("/devices")
    public String devices(@RequestParam(required = false) String search,
                          @RequestParam(required = false) String status,
                          Model model) {
        List<DeviceDTO> devices;
        try {
            if (search != null && !search.isBlank()) {
                devices = deviceService.searchDevices(search);
            } else if (status != null && !status.isBlank()) {
                devices = deviceService.getDevicesByStatus(Device.DeviceStatus.valueOf(status));
            } else {
                devices = deviceService.getAllDevices();
            }
        } catch (Exception e) {
            devices = List.of();
            model.addAttribute("error", "Could not load devices: " + e.getMessage());
        }
        model.addAttribute("devices", devices);
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        model.addAttribute("activePage", "devices");
        return "devices/list";
    }

    @GetMapping("/devices/add")
    public String addDeviceForm(Model model) {
        model.addAttribute("activePage", "devices");
        return "devices/form";
    }

    @PostMapping("/devices/add")
    public String addDevice(@ModelAttribute Device device, RedirectAttributes ra) {
        try {
            deviceService.createDevice(device);
            ra.addFlashAttribute("success", "Device added successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/devices";
    }

    @GetMapping("/devices/{id}/assign")
    public String assignForm(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("device", deviceService.getDeviceById(id));
            model.addAttribute("users", userService.getAllUsers());
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("activePage", "devices");
        return "devices/assign";
    }

    @PostMapping("/devices/{id}/assign")
    public String assignDevice(@PathVariable Long id,
                               @RequestParam Long userId,
                               @RequestParam(required = false, defaultValue = "") String notes,
                               RedirectAttributes ra) {
        try {
            deviceService.assignDevice(id, userId, getLoggedInUserId(), null, notes);
            ra.addFlashAttribute("success", "Device assigned successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/devices";
    }

    @PostMapping("/devices/{id}/return")
    public String returnDevice(@PathVariable Long id,
                               @RequestParam(required = false, defaultValue = "Returned") String notes,
                               RedirectAttributes ra) {
        try {
            deviceService.returnDevice(id, getLoggedInUserId(), notes);
            ra.addFlashAttribute("success", "Device returned successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/devices";
    }

    @PostMapping("/devices/{id}/repair")
    public String repairDevice(@PathVariable Long id,
                               @RequestParam(required = false, defaultValue = "Sent to repair") String notes,
                               RedirectAttributes ra) {
        try {
            deviceService.sendToRepair(id, getLoggedInUserId(), notes);
            ra.addFlashAttribute("success", "Device sent to repair.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/devices";
    }

    @PostMapping("/devices/{id}/lost")
    public String lostDevice(@PathVariable Long id,
                             @RequestParam(required = false, defaultValue = "Reported lost") String notes,
                             RedirectAttributes ra) {
        try {
            deviceService.markAsLost(id, getLoggedInUserId(), notes);
            ra.addFlashAttribute("success", "Device marked as lost.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/devices";
    }

    // ── Users ─────────────────────────────────────────────
    @GetMapping("/users")
    public String users(@RequestParam(required = false) String search, Model model) {
        List<UserDTO> users;
        try {
            users = (search != null && !search.isBlank())
                ? userService.searchUsers(search)
                : userService.getAllUsers();
        } catch (Exception e) {
            users = List.of();
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("users", users);
        model.addAttribute("search", search);
        model.addAttribute("activePage", "users");
        return "users/list";
    }

    @GetMapping("/users/add")
    public String addUserForm(Model model) {
        model.addAttribute("activePage", "users");
        return "users/form";
    }

    @PostMapping("/users/add")
    public String addUser(@ModelAttribute User user, RedirectAttributes ra) {
        try {
            userService.createUser(user);
            ra.addFlashAttribute("success", "User added successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/users";
    }

    // ── Transactions ──────────────────────────────────────
    @GetMapping("/transactions")
    public String transactions(Model model) {
        try {
            model.addAttribute("transactions", transactionService.getAllTransactions());
        } catch (Exception e) {
            model.addAttribute("transactions", List.of());
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("activePage", "transactions");
        return "transactions/list";
    }

    // ── Reports ───────────────────────────────────────────
    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("activePage", "reports");
        return "reports/index";
    }

    // ── Helper: get logged-in user's DB id ───────────────
    private Long getLoggedInUserId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            return userService.getAllUsers().stream()
                .filter(u -> username.equals(u.getUsername()))
                .findFirst()
                .map(UserDTO::getId)
                .orElse(1L);
        } catch (Exception e) {
            return 1L;
        }
    }
}