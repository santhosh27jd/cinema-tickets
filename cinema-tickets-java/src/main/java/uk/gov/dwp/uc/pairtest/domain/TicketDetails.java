package uk.gov.dwp.uc.pairtest.domain;

import java.util.UUID;

/**
 * Ticket Details
 */
public class TicketDetails {

    /**
     * Unique ticket id
     */
    private UUID ticketId;

    /**
     * Account id
     */
    private Long accountId;

    /**
     * Number of seats reserved
     */
    private int totalSeatsToAllocate;

    /**
     * Total amount paid
     */
    private int totalAmountToPay;

    /**
     *
     * @param ticketId
     * @param accountId
     * @param totalSeatsToAllocate
     * @param totalAmountToPay
     */
    public TicketDetails(UUID ticketId,
                  Long accountId,
                  int totalSeatsToAllocate,
                  int totalAmountToPay ){
        this.ticketId = ticketId;
        this.accountId = accountId;
        this.totalSeatsToAllocate = totalSeatsToAllocate;
        this.totalAmountToPay = totalAmountToPay;
    }

    /**
     *
     * @return total seats
     */
    public int getTotalSeatsToAllocate(){
        return totalSeatsToAllocate;
    }

    /**
     *
     * @return amount paid
     */
    public int getTotalAmountToPay(){
        return totalAmountToPay;
    }

}
