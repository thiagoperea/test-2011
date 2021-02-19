package com.thiagoperea.mybills.core.extension

import android.view.View
import io.mockk.mockk
import io.mockk.verifySequence
import org.junit.Assert
import org.junit.Test

class ExtensionsTest {

    @Test
    fun `test number formatMonetary`() {
        //Arrange
        val data = 5124

        //Act
        val formatted = data.formatMonetary()

        //Assert
        Assert.assertEquals("5,124.00", formatted)
    }

    @Test
    fun `test charSequence converToDouble`() {
        //Arrange
        val data = "R$ 123.45,56"

        //Act
        val formatted = data.convertToDouble()

        //Assert
        Assert.assertEquals(12345.56, formatted, 0.0)
    }

    @Test
    fun `test view extensions`() {
        //Arrange
        val viewMock = mockk<View>(relaxed = true)

        //Act
        viewMock.setVisible()
        viewMock.setHide()

        //Assert
        verifySequence {
            viewMock.visibility = View.VISIBLE
            viewMock.visibility = View.GONE
        }
    }
}