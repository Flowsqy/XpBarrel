package fr.flowsqy.xpbarrel.barrel;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class CustomPersistentDataType {

    public final static PersistentDataType<byte[], UUID> UUID = new UUIDType();

    private static class UUIDType implements PersistentDataType<byte[], UUID> {

        @Override
        @NotNull
        public Class<UUID> getComplexType() {
            return UUID.class;
        }

        @Override
        @NotNull
        public Class<byte[]> getPrimitiveType() {
            return byte[].class;
        }

        @Override
        @NotNull
        public UUID fromPrimitive(byte @NotNull [] primitive, @NotNull PersistentDataAdapterContext context) {
            final ByteBuffer bb = ByteBuffer.wrap(primitive);
            final long firstLong = bb.getLong();
            final long secondLong = bb.getLong();
            return new UUID(firstLong, secondLong);
        }

        @Override
        public byte @NotNull [] toPrimitive(@NotNull UUID complex, @NotNull PersistentDataAdapterContext context) {
            final ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
            bb.putLong(complex.getMostSignificantBits());
            bb.putLong(complex.getLeastSignificantBits());
            return bb.array();
        }

    }

}
