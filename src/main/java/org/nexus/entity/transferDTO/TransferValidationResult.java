package org.nexus.entity.transferDTO;

import org.nexus.entity.InventoryItem;

import java.util.List;


public class TransferValidationResult {


    private boolean valid;
    private List<String> messages;
    private List<InventoryItem> itemsToTransfer;


    public TransferValidationResult(boolean valid, List<String> messages, List<InventoryItem> itemsToTransfer) {
        this.valid = valid;
        this.messages = messages;
        this.itemsToTransfer = itemsToTransfer;
    }


    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public List<InventoryItem> getItemsToTransfer() {
        return itemsToTransfer;
    }

    public void setItemsToTransfer(List<InventoryItem> itemsToTransfer) {
        this.itemsToTransfer = itemsToTransfer;
    }
}
