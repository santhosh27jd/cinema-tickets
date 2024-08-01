package uk.gov.dwp.uc.pairtest.domain;

/**
 * Immutable Object
 */

public final class TicketTypeRequest {

    private int noOfTickets;
    private Type type;

    public TicketTypeRequest(Type type, int noOfTickets) {
        this.type = type;
        this.noOfTickets = noOfTickets;
    }

    public int getNoOfTickets() {
        return noOfTickets;
    }

    public Type getTicketType() {
        return type;
    }

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
