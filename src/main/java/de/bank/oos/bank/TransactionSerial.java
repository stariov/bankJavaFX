package de.bank.oos.bank;

import com.google.gson.*;

import java.lang.reflect.Type;

public class TransactionSerial implements JsonSerializer<Transaction>, JsonDeserializer<Transaction> {

    /**
     * Deserializes a {@link Transaction} object and returns it.
     * @param jsonElement
     * @param type
     * @param jsonDeserializationContext
     * @return transaction object
     * @throws JsonParseException
     */
    @Override
    public Transaction deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject container = jsonElement.getAsJsonObject();

        String classname = container.get("CLASSNAME").getAsString();
        JsonObject obj = container.getAsJsonObject("INSTANCE");

        String date = obj.get("date").getAsString();
        double amount = obj.get("amount").getAsDouble();
        String description = obj.get("description").getAsString();

        return switch (classname) {
            case "OutgoingTransfer" ->
                    new OutgoingTransfer(date, amount, description, obj.get("sender").getAsString(), obj.get("recipient").getAsString());
            case "IncomingTransfer" ->
                    new IncomingTransfer(date, amount, description, obj.get("sender").getAsString(), obj.get("recipient").getAsString());
            case "Payment" ->
                    new Payment(date, amount, description, obj.get("incomingInterest").getAsDouble(), obj.get("outgoingInterest").getAsDouble());
            default -> throw new JsonParseException("Type of transaction is unknown!");

        };
    }

    /**
     * Serializes {@link Transaction} object and returns {@link JsonElement} object.
     * @param transaction
     * @param type
     * @param jsonSerializationContext
     * @return serialized transaction as JsonElement
     */
    @Override
    public JsonElement serialize(Transaction transaction, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject container = new JsonObject();

        container.addProperty("CLASSNAME", transaction.getClass().getSimpleName());
        container.add("INSTANCE", jsonSerializationContext.serialize(transaction).getAsJsonObject());
        //  System.out.println("serial");
        return container;
    }
}
