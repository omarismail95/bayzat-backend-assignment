package com.bayzdelivery.controller;

import com.bayzdelivery.dto.DeliveryRequest;
import com.bayzdelivery.dto.DeliveryResponse;
import com.bayzdelivery.dto.TopDeliveryManResponse;
import com.bayzdelivery.service.DeliveryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DeliveryController.class)
class DeliveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DeliveryService deliveryService;

    @Test
    void createDelivery_shouldReturnCreatedDelivery() throws Exception {
        DeliveryRequest request = new DeliveryRequest();
        request.setDeliveryManId(1L);
        request.setCustomerId(2L);
        request.setStartTime(LocalDateTime.of(2026, 1, 1, 10, 0));
        request.setEndTime(LocalDateTime.of(2026, 1, 1, 11, 0));
        request.setDistance(new BigDecimal("10.50"));
        request.setPrice(new BigDecimal("20.00"));

        DeliveryResponse response = new DeliveryResponse();
        response.setId(100L);
        response.setDeliveryManId(1L);
        response.setCustomerId(2L);
        response.setStartTime(request.getStartTime());
        response.setEndTime(request.getEndTime());
        response.setDistance(new BigDecimal("10.50"));
        response.setPrice(new BigDecimal("20.00"));
        response.setCommission(new BigDecimal("6.25"));

        when(deliveryService.createDelivery(any(DeliveryRequest.class))).thenReturn(response);

        mockMvc.perform(post("/deliveries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.deliveryManId").value(1))
                .andExpect(jsonPath("$.customerId").value(2))
                .andExpect(jsonPath("$.distance").value(10.50))
                .andExpect(jsonPath("$.price").value(20.00))
                .andExpect(jsonPath("$.commission").value(6.25));

        verify(deliveryService).createDelivery(any(DeliveryRequest.class));
    }

    @Test
    void getDeliveryById_shouldReturnDelivery() throws Exception {
        DeliveryResponse response = new DeliveryResponse();
        response.setId(100L);
        response.setDeliveryManId(1L);
        response.setCustomerId(2L);
        response.setStartTime(LocalDateTime.of(2026, 1, 1, 10, 0));
        response.setEndTime(LocalDateTime.of(2026, 1, 1, 11, 0));
        response.setDistance(new BigDecimal("10.50"));
        response.setPrice(new BigDecimal("20.00"));
        response.setCommission(new BigDecimal("6.25"));

        when(deliveryService.findById(100L)).thenReturn(response);

        mockMvc.perform(get("/deliveries/{deliveryId}", 100L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.deliveryManId").value(1))
                .andExpect(jsonPath("$.customerId").value(2))
                .andExpect(jsonPath("$.commission").value(6.25));

        verify(deliveryService).findById(100L);
    }

    @Test
    void getTopDeliveryMen_shouldReturnTopPerformers() throws Exception {
        LocalDateTime startTime = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2026, 1, 31, 23, 59);

        TopDeliveryManResponse first = new TopDeliveryManResponse();
        first.setDeliveryManId(1L);
        first.setDeliveryManName("Ali");
        first.setTotalCommission(new BigDecimal("150.00"));
        first.setAverageCommission(new BigDecimal("50.00"));

        TopDeliveryManResponse second = new TopDeliveryManResponse();
        second.setDeliveryManId(2L);
        second.setDeliveryManName("Omar");
        second.setTotalCommission(new BigDecimal("120.00"));
        second.setAverageCommission(new BigDecimal("40.00"));

        when(deliveryService.getTopDeliveryMen(startTime, endTime))
                .thenReturn(List.of(first, second));

        mockMvc.perform(get("/deliveries/top-performers")
                        .param("startTime", "2026-01-01T00:00:00")
                        .param("endTime", "2026-01-31T23:59:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].deliveryManId").value(1))
                .andExpect(jsonPath("$[0].deliveryManName").value("Ali"))
                .andExpect(jsonPath("$[0].totalCommission").value(150.00))
                .andExpect(jsonPath("$[0].averageCommission").value(50.00))
                .andExpect(jsonPath("$[1].deliveryManId").value(2))
                .andExpect(jsonPath("$[1].deliveryManName").value("Omar"));

        verify(deliveryService).getTopDeliveryMen(eq(startTime), eq(endTime));
    }

    @Test
    void getTopDeliveryMen_whenStartTimeAfterEndTime_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/deliveries/top-performers")
                        .param("startTime", "2026-02-01T00:00:00")
                        .param("endTime", "2026-01-01T00:00:00"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createDelivery_whenRequestInvalid_shouldReturnBadRequest() throws Exception {
        DeliveryRequest request = new DeliveryRequest();

        mockMvc.perform(post("/deliveries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
