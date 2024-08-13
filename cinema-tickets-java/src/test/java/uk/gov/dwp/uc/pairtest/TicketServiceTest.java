package uk.gov.dwp.uc.pairtest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;

import uk.gov.dwp.uc.pairtest.cache.CacheManager;
import uk.gov.dwp.uc.pairtest.domain.TicketDetails;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;


/**
 * Ticket service test class
 */
public class TicketServiceTest {

    /**
     * Ticket service object
     */
    TicketServiceImpl ticketService;

    /**
     * CacheManager to store ticket reservation based on account id
     */
    CacheManager<Long,TicketDetails> cacheManager = new CacheManager<>();
    @Before
    public void setup() {
        ticketService = new TicketServiceImpl(cacheManager);
    }

    @After
    public void clean(){
        cacheManager.clear();
    }

    /**
     * Test case to check ticket count exceed maximum limit
     */
    @Test
    public void ticketCountExceedMaximumLimit_Throw_Exception(){
        long accountId = 100;
        TicketTypeRequest ticketTypeRequestAdult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT,21);
        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, ()->ticketService.purchaseTickets(accountId,ticketTypeRequestAdult));
        assertEquals("Maximum ticket count exceeded.", exception.getMessage());
    }

    /**
     *
     */
    @Test
    public void accountIdIsNull_Throw_Exception(){
        Long accountId = null;
        TicketTypeRequest ticketTypeRequestAdult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT,3);
        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, ()->ticketService.purchaseTickets(accountId,ticketTypeRequestAdult));
        assertEquals("Account id is not valid.", exception.getMessage());
    }

    /**
     * Invalid ticket purchase
     */
    @Test
    public void invalidPurchaseTicket_Throw_Exception(){
        long accountId = 12;
        TicketTypeRequest ticketTypeRequestChild = new TicketTypeRequest(TicketTypeRequest.Type.INFANT,1);
        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, ()->ticketService.purchaseTickets(accountId,ticketTypeRequestChild));
        assertEquals("Invalid ticket request.", exception.getMessage());
    }

    /**
     * Infant without adult ticket purchase
     */
    @Test
    public void infantWithoutAdultPurchaseTicket_Throw_Exception(){
        long accountId = 140;
        TicketTypeRequest ticketTypeRequestChild = new TicketTypeRequest(TicketTypeRequest.Type.CHILD,1);
        TicketTypeRequest ticketTypeRequestInfant = new TicketTypeRequest(TicketTypeRequest.Type.INFANT,1);
        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, ()->ticketService.purchaseTickets(accountId,ticketTypeRequestChild,ticketTypeRequestInfant));
        assertEquals("Please add atleast one adult.", exception.getMessage());
    }

    /**
     * Child without adult ticket purchase
     */
    @Test
    public void childWithoutAdultPurchaseTicket_Throw_Exception(){
        long accountId = 70;
        TicketTypeRequest ticketTypeRequestChild = new TicketTypeRequest(TicketTypeRequest.Type.CHILD,1);
        TicketTypeRequest ticketTypeRequestInfant = new TicketTypeRequest(TicketTypeRequest.Type.INFANT,1);
        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, ()->ticketService.purchaseTickets(accountId,ticketTypeRequestChild,ticketTypeRequestInfant));
        assertEquals("Please add atleast one adult.", exception.getMessage());
    }

    /**
     * Infant and Child without adult ticket purchase
     */
    @Test
    public void infantAndChildWithoutAdultPurchaseTicket_Throw_Exception(){
        long accountId = 99;
        TicketTypeRequest ticketTypeRequestInfant = new TicketTypeRequest(TicketTypeRequest.Type.INFANT,1);
        TicketTypeRequest ticketTypeRequestChild = new TicketTypeRequest(TicketTypeRequest.Type.CHILD,1);

        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, ()->ticketService.purchaseTickets(accountId,ticketTypeRequestInfant,ticketTypeRequestChild));
        assertEquals("Please add atleast one adult.", exception.getMessage());
    }

    /**
     * Invalid ticket request
     */
    @Test
    public void ifTicketDetailsIsMissing_Throw_Exception(){
        long accountId = 45;
        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, ()->ticketService.purchaseTickets(accountId));
        assertEquals("Invalid ticket request.", exception.getMessage());
    }

    /**
     * Account id is not valid
     */
    @Test
    public void ifAccountIdIsNotValid_Throw_Exception(){
        long accountId = -1;
        TicketTypeRequest ticketTypeRequestAdult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT,2);
        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, ()->ticketService.purchaseTickets(accountId,ticketTypeRequestAdult));
        assertEquals("Account id is not valid.", exception.getMessage());
    }

    /**
     * Do not pay for Infant ticket
     */
    @Test
    public void doNotPayForInfantTicket_success(){
        long accountId = 66;
        TicketTypeRequest ticketTypeRequestAdult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT,2);
        TicketTypeRequest ticketTypeRequestInfant = new TicketTypeRequest(TicketTypeRequest.Type.INFANT,1);
        ticketService.purchaseTickets(accountId,ticketTypeRequestAdult,ticketTypeRequestInfant);
        TicketDetails ticketDetails = cacheManager.get(accountId);
        assertEquals(40,ticketDetails.getTotalAmountToPay());
    }

    /**
     * Do not allocate seat for Infant
     */
    @Test
    public void doNotAllocateSeatForInfantTicket_success(){
        long accountId = 77;
        TicketTypeRequest ticketTypeRequestAdult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT,2);
        TicketTypeRequest ticketTypeRequestInfant = new TicketTypeRequest(TicketTypeRequest.Type.INFANT,1);
        ticketService.purchaseTickets(accountId,ticketTypeRequestAdult,ticketTypeRequestInfant);
        TicketDetails ticketDetails = cacheManager.get(accountId);
        assertEquals(2,ticketDetails.getTotalSeatsToAllocate());
    }

    /**
     * Seat reservation success
     */
    @Test
    public void seatReservationForAccountId_success(){
        long accountId = 34;
        TicketTypeRequest ticketTypeRequestAdult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT,2);
        TicketTypeRequest ticketTypeRequestChild = new TicketTypeRequest(TicketTypeRequest.Type.CHILD,1);
        TicketTypeRequest ticketTypeRequestInfant = new TicketTypeRequest(TicketTypeRequest.Type.INFANT,1);
        ticketService.purchaseTickets(accountId,ticketTypeRequestAdult,ticketTypeRequestChild,ticketTypeRequestInfant);
        TicketDetails ticketDetails = cacheManager.get(accountId);
        assertEquals(3,ticketDetails.getTotalSeatsToAllocate());
    }

    /**
     * Amount paid and ticket purchase success
     */
    @Test
    public void totalAmountPaymentForAccountId_success(){
        long accountId = 560;
        TicketTypeRequest ticketTypeRequestAdult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT,2);
        TicketTypeRequest ticketTypeRequestChild = new TicketTypeRequest(TicketTypeRequest.Type.CHILD,1);
        TicketTypeRequest ticketTypeRequestInfant = new TicketTypeRequest(TicketTypeRequest.Type.INFANT,1);
        ticketService.purchaseTickets(accountId,ticketTypeRequestAdult,ticketTypeRequestChild,ticketTypeRequestInfant);
        TicketDetails ticketDetails = cacheManager.get(accountId);
        assertEquals(50,ticketDetails.getTotalAmountToPay());
    }

    /**
     * Multiple ticket purchase
     */
    @Test
    public void multiple_ticket_purchase_success(){
        long accountId = 499;
        TicketTypeRequest ticketTypeRequestAdult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT,2);
        TicketTypeRequest ticketTypeRequestChild = new TicketTypeRequest(TicketTypeRequest.Type.CHILD,1);
        TicketTypeRequest ticketTypeRequestInfant = new TicketTypeRequest(TicketTypeRequest.Type.INFANT,1);
        ticketService.purchaseTickets(accountId,ticketTypeRequestAdult,ticketTypeRequestChild,ticketTypeRequestInfant);
    }

    /**
     * Ticket purchase success
     */
    @Test
    public void ticket_purchase_success(){
        long accountId = 777;
        TicketTypeRequest ticketTypeRequestAdult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT,1);
        ticketService.purchaseTickets(accountId,ticketTypeRequestAdult);
    }
    /**
     * Ticket purchase success with Wrapper account id
     */
    @Test
    public void ticket_purchase_success_with_wrapper(){
        Long accountId = Long.valueOf(678);
        TicketTypeRequest ticketTypeRequestAdult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT,1);
        ticketService.purchaseTickets(accountId,ticketTypeRequestAdult);
    }

}
