package com.raki.pos.staff;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @GetMapping("/me/business")
    public ResponseEntity<List<StaffDTO>> getStaffForMyBusiness() {
        try {
            List<StaffDTO> staff = staffService.listStaffForCurrentBusiness();
            return ResponseEntity.ok(staff);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<StaffDTO> updateStaffMember(
            @PathVariable Integer id,
            @RequestBody StaffUpdateRequest req
    ) {
        try {
            return staffService.updateStaffForCurrentBusiness(
                            id,
                            req.getName(),
                            req.getEmail(),
                            req.getPhone(),
                            req.getPassword(),
                            req.getRole(),
                            req.getStatus()
                    )
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        }
    }
}
