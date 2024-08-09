package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.cache.CacheManager;
import uk.gov.dwp.uc.pairtest.domain.TicketDetails;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */

    /**
     * CacheManager to store ticket information
     */
    private CacheManager<Long,TicketDetails> cache;

    /**
     * Cache injected in constructor for testing
     * @param cache
     */
    TicketServiceImpl(CacheManager<Long,TicketDetails> cache){
        this.cache = cache;
    }
    /**
     * MAX_TICKET_COUNT
     */
    private int MAX_TICKET_COUNT = 20;

    /**
     *  SeatReservationServiceImpl For Seat Reservation
     */
    private SeatReservationServiceImpl seatReservationService = new SeatReservationServiceImpl();

    /**
     * TicketPaymentServiceImpl for ticket payments
     */
    private TicketPaymentServiceImpl ticketPaymentService = new TicketPaymentServiceImpl();

    /**
     * To check ticket count is exceeded the maximum ticket count
     * @param length
     * @return
     */
    private boolean isTicketCountExceeded(int length){
        if(length > MAX_TICKET_COUNT){
            return true;
        }
        return false;
    }

    /**
     *  Calculate total seats to allocate for reservation
     * @param ticketRequest
     * @return
     */
    private int totalSeatsToAllocate(Map<TicketTypeRequest.Type, Integer> ticketRequest){
        return ticketRequest.entrySet().stream().filter(e -> e.getKey().getPrice() != 0).map(Map.Entry::getValue).reduce(0,Integer::sum);
    }

    /**
     * Calculate total amount to pay for the reserved seats
     * @param ticketRequest
     * @return
     */
    private int totalAmountToPay(Map<TicketTypeRequest.Type, Integer> ticketRequest){
        return ticketRequest.entrySet().stream().filter(e -> e.getKey().getPrice() != 0).map(e -> e.getKey().getPrice() * e.getValue()).reduce(0,Integer::sum);
    }

    /**
     *
     * @param accountId
     * @param ticketTypeRequests
     * @throws InvalidPurchaseException
     */
    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        // Account id validation check.

        if(accountId == null){
            throw new InvalidPurchaseException("Account id is not valid.");
        }

        if(accountId <= 0){
            throw new InvalidPurchaseException("Account id is not valid.");
        }

        // Checking the request input to process ticket reservation.
        List<TicketTypeRequest> ticketTypeRequestsList = Arrays.asList(ticketTypeRequests);
        if(ticketTypeRequestsList.isEmpty()){
            throw new InvalidPurchaseException("Invalid ticket request.");
        }

        // Checking invalid ticket request.
        Map<TicketTypeRequest.Type, Integer> ticketRequestMap = ticketTypeRequestsList.stream().collect(Collectors.toMap(TicketTypeRequest::getTicketType,TicketTypeRequest::getNoOfTickets));
        if(ticketTypeRequestsList.size() == 1 && ticketRequestMap.containsKey(TicketTypeRequest.Type.INFANT)){
            throw new InvalidPurchaseException("Invalid ticket request.");
        }
        if(ticketTypeRequestsList.size() == 1 && ticketRequestMap.containsKey(TicketTypeRequest.Type.CHILD)){
            throw new InvalidPurchaseException("Invalid ticket request.");
        }

        // Checking Child and Infant tickets cannot be purchased without purchasing an Adult ticket
        if(ticketRequestMap.containsKey(TicketTypeRequest.Type.INFANT) || ticketRequestMap.containsKey(TicketTypeRequest.Type.CHILD)){
            if(!ticketRequestMap.containsKey(TicketTypeRequest.Type.ADULT)){
                throw new InvalidPurchaseException("Please add atleast one adult.");
            }
        }

        // Get total seats to allocate for ticket purchase.
        int totalSeatsToAllocate = totalSeatsToAllocate(ticketRequestMap);
        if(isTicketCountExceeded(totalSeatsToAllocate)){
            throw new InvalidPurchaseException("Maximum ticket count exceeded.");
        }

        // Get total amount to pay for ticket purchase.
        int totalAmountToPay = totalAmountToPay(ticketRequestMap);
        // Seat reservation request.
        seatReservationService.reserveSeat(accountId, totalSeatsToAllocate);
        // Payment request.
        ticketPaymentService.makePayment(accountId,totalAmountToPay);

        // Storing ticket details.
        cache.put(accountId,new TicketDetails(UUID.randomUUID(),accountId,totalSeatsToAllocate,totalAmountToPay));
    }

}
