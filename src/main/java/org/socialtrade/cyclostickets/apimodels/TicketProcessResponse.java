package org.socialtrade.cyclostickets.apimodels;

/**
 * Response after processing a ticket
 */
public class TicketProcessResponse {
    private boolean actuallyProcessed;
    private Transaction transaction;

    public boolean isActuallyProcessed() {
        return actuallyProcessed;
    }

    public void setActuallyProcessed(boolean actuallyProcessed) {
        this.actuallyProcessed = actuallyProcessed;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
}
