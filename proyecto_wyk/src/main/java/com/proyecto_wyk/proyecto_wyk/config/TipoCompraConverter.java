package com.proyecto_wyk.proyecto_wyk.config;

import com.proyecto_wyk.proyecto_wyk.entity.Compra;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false) // No aplicamos automáticamente para solo usarlo en la Entidad Compra
public class TipoCompraConverter implements AttributeConverter<Compra.TipoCompra, String> {

    // Método 1: De Java (Entity) a BD (Column)
    @Override
    public String convertToDatabaseColumn(Compra.TipoCompra attribute) {
        if (attribute == null) {
            return null;
        }
        // Convierte MATERIA_PRIMA (Java) a "MATERIA PRIMA" (BD)
        return attribute.name().replace("_", " ");
    }

    // Método 2: De BD (Column) a Java (Entity)
    @Override
    public Compra.TipoCompra convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        // Convierte "MATERIA PRIMA" (DB) a MATERIA_PRIMA (Java)
        return Compra.TipoCompra.valueOf(dbData.replace(" ", "_").toUpperCase());
    }
}
