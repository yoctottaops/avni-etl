package org.avniproject.etl.domain.metadata;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ColumnTest {
    @Test
    public void lessThanMaxSizeColumnName() {
        String lessThanMaxSizeColumnName = "Total silt requested by the family members";
        Column column = new Column(lessThanMaxSizeColumnName, Column.Type.numeric);
        assertEquals(lessThanMaxSizeColumnName, column.getName());
    }

    @Test
    void maxSizeColumnName() {
        String maxSizeColumnName = "Total silt requested by the family members – Numb of trolleys";
        Column column = new Column(maxSizeColumnName, Column.Type.numeric);
        assertEquals(maxSizeColumnName, column.getName());
    }

    @Test
    public void biggerThanMaxSizedColumnName() {
        String biggerThanMaxSizedColumnName = "Total silt requested by the family members – Number of trolleys";
        Column column = new Column(biggerThanMaxSizedColumnName, Column.Type.numeric);
        assertEquals("Total silt requested by the family members – Nu (1206887472)", column.getName());
    }
}
