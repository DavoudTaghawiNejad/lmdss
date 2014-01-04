package messages;
import agents.Firm;


public final class FirmQuantity {
    public final Firm firm;

    public final double quantity;

    public FirmQuantity(Firm firm, double quantity) {
    	this.firm = firm;
    	this.quantity = quantity;
    }
} 
