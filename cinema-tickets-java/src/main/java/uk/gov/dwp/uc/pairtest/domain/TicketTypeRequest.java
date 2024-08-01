package uk.gov.dwp.uc.pairtest.domain;

/**
 * Immutable Object
 */

public final class TicketTypeRequest {

    /**
     * Number of tickets
     */
    private int noOfTickets;

    /**
     * Type of tickets (ADULT,CHILD,INFANT)
     */
    private Type type;

    public TicketTypeRequest(Type type, int noOfTickets) {
        this.type = type;
        this.noOfTickets = noOfTickets;
    }

    /**
     *
     * @return
     */
    public int getNoOfTickets() {
        return noOfTickets;
    }

    /**
     *
     * @return
     */
    public Type getTicketType() {
        return type;
    }

    /**
     * ENUM
     */
    public enum Type {
        ADULT(20), CHILD(10) , INFANT(0);
        private int price;
        Type(int price){
            this.price = price;
        }
        public int getPrice(){
            return price;
        }
    }

}
