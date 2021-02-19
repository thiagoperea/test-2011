package com.thiagoperea.mybills.core.converter

import org.junit.Assert.assertEquals
import org.junit.Test

class DateConverterTest {

    @Test
    fun `test getDateFromString`() {
        //Arrange
        val toFormat = "02-01-2000"

        //Act
        val formattedDate = DateConverter.getDateFromString(toFormat)

        //Assert
        assertEquals(0, formattedDate?.month)
        assertEquals(2, formattedDate?.date)
        assertEquals(100, formattedDate?.year)
    }

    @Test
    fun `test getStringFromDate`() {
        //Arrange
        val date = DateConverter.getDateFromString("02-01-2000")

        //Act
        val formatted1 = DateConverter.getStringFromDate(date, DateConverter.FORMAT_DD_MM_YYYY)
        val formatted2 = DateConverter.getStringFromDate(date, DateConverter.FORMAT_RAW)

        //Assert
        assertEquals("02/01/2000", formatted1)
        assertEquals("02-01-2000", formatted2)
    }

    @Test
    fun `test formatTime`() {
        //Arrange
        val hour = 3
        val minute = 7

        //Act
        val formatted = DateConverter.formatTime(hour, minute)

        //Assert
        assertEquals("03:07", formatted)
    }
}