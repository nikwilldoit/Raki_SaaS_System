package com.raki.pos.reservation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import java.util.List;


/**
 * Service class for managing reservations.
 * Provides methods for creating, retrieving, updating, and deleting reservations.
 */
@Service
public class ResService {

    private static final Logger logger = LogManager.getLogger(ResService.class);

    private final ResRepository resRepository;

    /**
     * Constructor for ResService.
     *
     * @param resRepository the repository used for reservation data access
     */
    public ResService(ResRepository resRepository) {
        this.resRepository = resRepository;
    }

    /**
     * Retrieves a list of reservations for a specific business.
     *
     * @param businessId the ID of the business
     * @return a list of reservation data transfer objects
     */
    public List<ResDTO.ReservationListResponseDTO> getReservations(int businessId) {
        return resRepository.fetchReservationListData(businessId);
    }

    /**
     * Retrieves reservation creation data for a specific business.
     *
     * @param businessId the ID of the business
     * @return a data transfer object containing reservation creation data
     */
    public ResDTO.ReservationCreationDataResponseDTO getBusinessReservationData(int businessId) {
        return new ResDTO.ReservationCreationDataResponseDTO(resRepository.fetchReservationCreationData(businessId));
    }

    /**
     * Fetches availability for a specific business and optionally an employee on a given date.
     *
     * @param businessId the ID of the business
     * @param employeeId the ID of the employee (optional, use 0 if not applicable)
     * @param date       the date for which availability is being checked
     * @return a list of available time slots
     */
    public List<String> fetchAvailability(int businessId, int employeeId, String date) {
        if (employeeId == 0) {
            return resRepository.fetchAvailability(businessId, date);
        }
        return resRepository.fetchAvailability(businessId, employeeId, date);
    }

    /**
     * Creates a new reservation.
     *
     * TODO: Add input validation for the parameters.
     *
     * @param customerPhone the phone number of the customer
     * @param customerName  the name of the customer
     * @param date          the date of the reservation
     * @param businessId    the ID of the business
     * @param serviceId     the ID of the service
     * @param employeeId    the ID of the employee
     * @return the ID of the created reservation
     */
    public String createReservation(String customerPhone, String customerName, String date, int businessId, int serviceId, int employeeId) {
        return resRepository.createReservation(customerPhone, customerName, date, businessId, serviceId, employeeId);
    }

    /**
     * Deletes a reservation for a specific business.
     *
     * @param reservationId the ID of the reservation to delete
     * @param businessId    the ID of the business
     * @throws EmptyResultDataAccessException if the reservation is not found or unauthorized
     */
    public void deleteReservation(int reservationId, int businessId) {
        int rows = resRepository.deleteReservation(reservationId, businessId);
        if (rows == 0) {
            logger.error("Failed to delete reservation with ID {} for business ID {}", reservationId, businessId);
            throw new EmptyResultDataAccessException("Reservation not found or unauthorized", 1);
        }
    }

    /**
     * Updates an existing reservation.
     *
     * @param reservationId the ID of the reservation to update
     * @param updateData    the data transfer object containing updated reservation details
     * @throws RuntimeException if the reservation is not found or unauthorized
     */
    public void updateReservation(int reservationId, ResDTO.ReservationUpdateRequestDTO updateData) {
        int rows = resRepository.updateReservation(
                reservationId,
                updateData.getBusinessId(),
                updateData.getStatus(),
                updateData.getCustomerName(),
                updateData.getCustomerPhone()
        );
        if (rows == 0) {
            throw new RuntimeException("Reservation not found or unauthorized");
        }
    }
}