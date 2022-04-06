package org.avniproject.etl.domain;

import org.junit.jupiter.api.Test;

import static org.avniproject.etl.domain.NullObject.isNullObject;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class NullObjectTest {
    @Test
    void shouldSayANullObjectWhenItIsOne() {
        assertThat(isNullObject(NullObject.instance()), is(true));
        assertThat(isNullObject("I am not a null object"), is(false));
    }

}