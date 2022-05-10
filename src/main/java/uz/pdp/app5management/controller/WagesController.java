package uz.pdp.app5management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.app5management.entity.Wages;
import uz.pdp.app5management.payload.ApiResponse;
import uz.pdp.app5management.payload.WagesDto;
import uz.pdp.app5management.service.WagesService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/wages")
public class WagesController {

    @Autowired
    WagesService wagesService;

    @PostMapping
    public HttpEntity<?> addWages(@RequestBody WagesDto wagesDto){
        ApiResponse apiResponse = wagesService.addWages(wagesDto);
        return ResponseEntity.status(apiResponse.isSuccess() ? 201 : 409).body(apiResponse);
    }

    @GetMapping("/byUserId/{userId}")
    public HttpEntity<?> getWages(@PathVariable UUID userId){
        List<Wages> wages = wagesService.getWagesByUserId(userId);
        return ResponseEntity.ok(wages);
    }

    @GetMapping("/byPrice/{price}")
    public HttpEntity<?> getWagesByPrice(@PathVariable double price){
        return ResponseEntity.ok(wagesService.getWagesByPrice(price));
    }

    @PutMapping("/{id}")
    public HttpEntity<?> editWages(@PathVariable UUID id, @RequestBody WagesDto wagesDto){
        ApiResponse apiResponse = wagesService.editWages(id, wagesDto);
        return ResponseEntity.status(apiResponse.isSuccess() ? 202 : 409).body(apiResponse);
    }

    @DeleteMapping("/{id}")
    public HttpEntity<?> deleteWages(@PathVariable UUID id){
        ApiResponse apiResponse = wagesService.deleteWages(id);
        return ResponseEntity.status(apiResponse.isSuccess() ? 204 : 409).body(apiResponse);
    }

}










