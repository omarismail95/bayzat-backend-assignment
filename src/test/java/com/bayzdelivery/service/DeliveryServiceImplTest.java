package com.bayzdelivery.service;

import com.bayzdelivery.dto.DeliveryRequest;
import com.bayzdelivery.dto.DeliveryResponse;
import com.bayzdelivery.dto.TopDeliveryManResponse;
import com.bayzdelivery.exceptions.BusinessRuleException;
import com.bayzdelivery.exceptions.ResourceNotFoundException;
import com.bayzdelivery.model.Delivery;
import com.bayzdelivery.model.Person;
import com.bayzdelivery.model.PersonRole;
import com.bayzdelivery.repositories.DeliveryRepository;
import com.bayzdelivery.repositories.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceImplTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private DeliveryServiceImpl deliveryService;

    private Person deliveryMan;
    private Person customer;
    private DeliveryRequest request;

    @BeforeEach
    void setUp() {
        deliveryMan = new Person();
        deliveryMan.setId(1L);
        deliveryMan.setName("Omar");
        deliveryMan.setEmail("omar@example.com");
        deliveryMan.setRole(PersonRole.DELIVERY_MAN);

        customer = new Person();
        customer.setId(2L);
        customer.setName("Ali");
        customer.setEmail("ali@example.com");
        customer.setRole(PersonRole.CUSTOMER);

        request = new DeliveryRequest();
        request.setDeliveryManId(1L);
        request.setCustomerId(2L);
        request.setStartTime(LocalDateTime.of(2026, 1, 1, 10, 0));
        request.setEndTime(LocalDateTime.of(2026, 1, 1, 11, 0));
        request.setDistance(new BigDecimal("10.00"));
        request.setPrice(new BigDecimal("100.00"));
    }

    @Test
    void createDelivery_shouldCreateDeliveryAndCalculateCommission() {
        Delivery savedDelivery = new Delivery();
        savedDelivery.setId(100L);
        savedDelivery.setDeliveryMan(deliveryMan);
        savedDelivery.setCustomer(customer);
        savedDelivery.setStartTime(request.getStartTime());
        savedDelivery.setEndTime(request.getEndTime());
        savedDelivery.setDistance(request.getDistance());
        savedDelivery.setPrice(request.getPrice());
        savedDelivery.setCommission(new BigDecimal("10.00"));

        when(personRepository.findById(1L)).thenReturn(Optional.of(deliveryMan));
        when(personRepository.findById(2L)).thenReturn(Optional.of(customer));
        when(deliveryRepository.existsOverlappingDelivery(
                1L,
                request.getStartTime(),
                request.getEndTime()
        )).thenReturn(false);
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(savedDelivery);

        DeliveryResponse response = deliveryService.createDelivery(request);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(1L, response.getDeliveryManId());
        assertEquals(2L, response.getCustomerId());
        assertEquals(new BigDecimal("10.00"), response.getCommission());

        ArgumentCaptor<Delivery> deliveryCaptor = ArgumentCaptor.forClass(Delivery.class);
        verify(deliveryRepository).save(deliveryCaptor.capture());

        Delivery capturedDelivery = deliveryCaptor.getValue();
        assertEquals(deliveryMan, capturedDelivery.getDeliveryMan());
        assertEquals(customer, capturedDelivery.getCustomer());
        assertEquals(request.getStartTime(), capturedDelivery.getStartTime());
        assertEquals(request.getEndTime(), capturedDelivery.getEndTime());
        assertEquals(new BigDecimal("10.00"), capturedDelivery.getDistance());
        assertEquals(new BigDecimal("100.00"), capturedDelivery.getPrice());
        assertEquals(new BigDecimal("10.00"), capturedDelivery.getCommission());
    }

    @Test
    void createDelivery_whenDeliveryManNotFound_shouldThrowResourceNotFoundException() {
        when(personRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> deliveryService.createDelivery(request)
        );

        assertEquals("Delivery man not found with id: 1", exception.getMessage());

        verify(personRepository).findById(1L);
        verify(deliveryRepository, never()).save(any());
    }

    @Test
    void createDelivery_whenPersonIsNotDeliveryMan_shouldThrowBusinessRuleException() {
        deliveryMan.setRole(PersonRole.CUSTOMER);

        when(personRepository.findById(1L)).thenReturn(Optional.of(deliveryMan));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> deliveryService.createDelivery(request)
        );

        assertEquals(
                "Person with id 1 is not registered as a delivery man",
                exception.getMessage()
        );

        verify(deliveryRepository, never()).save(any());
    }

    @Test
    void createDelivery_whenCustomerNotFound_shouldThrowResourceNotFoundException() {
        when(personRepository.findById(1L)).thenReturn(Optional.of(deliveryMan));
        when(personRepository.findById(2L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> deliveryService.createDelivery(request)
        );

        assertEquals("Customer not found with id: 2", exception.getMessage());

        verify(deliveryRepository, never()).save(any());
    }

    @Test
    void createDelivery_whenPersonIsNotCustomer_shouldThrowBusinessRuleException() {
        customer.setRole(PersonRole.DELIVERY_MAN);

        when(personRepository.findById(1L)).thenReturn(Optional.of(deliveryMan));
        when(personRepository.findById(2L)).thenReturn(Optional.of(customer));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> deliveryService.createDelivery(request)
        );

        assertEquals(
                "Person with id 2 is not registered as a customer",
                exception.getMessage()
        );

        verify(deliveryRepository, never()).save(any());
    }

    @Test
    void createDelivery_whenOverlappingDeliveryExists_shouldThrowBusinessRuleException() {
        when(personRepository.findById(1L)).thenReturn(Optional.of(deliveryMan));
        when(personRepository.findById(2L)).thenReturn(Optional.of(customer));
        when(deliveryRepository.existsOverlappingDelivery(
                1L,
                request.getStartTime(),
                request.getEndTime()
        )).thenReturn(true);

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> deliveryService.createDelivery(request)
        );

        assertEquals(
                "Delivery man already has an active delivery during the requested time window",
                exception.getMessage()
        );

        verify(deliveryRepository, never()).save(any());
    }

    @Test
    void findById_whenDeliveryExists_shouldReturnDeliveryResponse() {
        Delivery delivery = new Delivery();
        delivery.setId(100L);
        delivery.setDeliveryMan(deliveryMan);
        delivery.setCustomer(customer);
        delivery.setStartTime(request.getStartTime());
        delivery.setEndTime(request.getEndTime());
        delivery.setDistance(new BigDecimal("10.00"));
        delivery.setPrice(new BigDecimal("100.00"));
        delivery.setCommission(new BigDecimal("10.00"));

        when(deliveryRepository.findById(100L)).thenReturn(Optional.of(delivery));

        DeliveryResponse response = deliveryService.findById(100L);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(1L, response.getDeliveryManId());
        assertEquals(2L, response.getCustomerId());
        assertEquals(new BigDecimal("10.00"), response.getCommission());

        verify(deliveryRepository).findById(100L);
    }

    @Test
    void findById_whenDeliveryDoesNotExist_shouldThrowResourceNotFoundException() {
        when(deliveryRepository.findById(100L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> deliveryService.findById(100L)
        );

        assertEquals("Delivery not found with id: 100", exception.getMessage());

        verify(deliveryRepository).findById(100L);
    }

    @Test
    void getTopDeliveryMen_shouldReturnMappedTopDeliveryMenResponses() {
        LocalDateTime startTime = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2026, 1, 31, 23, 59);

        Object[] firstRow = {
                1L,
                "Omar",
                new BigDecimal("150.456"),
                new BigDecimal("50.123")
        };

        Object[] secondRow = {
                2L,
                "Ali",
                new BigDecimal("120.444"),
                new BigDecimal("40.555")
        };

        when(deliveryRepository.findTopDeliveryMenByCommission(startTime, endTime))
                .thenReturn(List.of(firstRow, secondRow));

        List<TopDeliveryManResponse> responses =
                deliveryService.getTopDeliveryMen(startTime, endTime);

        assertEquals(2, responses.size());

        assertEquals(1L, responses.get(0).getDeliveryManId());
        assertEquals("Omar", responses.get(0).getDeliveryManName());
        assertEquals(new BigDecimal("150.46"), responses.get(0).getTotalCommission());
        assertEquals(new BigDecimal("50.12"), responses.get(0).getAverageCommission());

        assertEquals(2L, responses.get(1).getDeliveryManId());
        assertEquals("Ali", responses.get(1).getDeliveryManName());
        assertEquals(new BigDecimal("120.44"), responses.get(1).getTotalCommission());
        assertEquals(new BigDecimal("40.56"), responses.get(1).getAverageCommission());

        verify(deliveryRepository).findTopDeliveryMenByCommission(startTime, endTime);
    }
}
