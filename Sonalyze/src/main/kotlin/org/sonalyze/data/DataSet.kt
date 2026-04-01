package org.sonalyze.data

import org.apache.commons.csv.CSVFormat
import smile.data.DataFrame
import smile.data.formula.Formula
import smile.io.Read
import smile.regression.LinearModel
import smile.regression.OLS
import java.io.File

/**
 * DataSet is Sonalyze's wrapper class for SMILE's DataFrame object which has utility functions and is
 * more tailored to Sonalyze's design.
 *
 * @property file The CSV file that represents the dataset
 */
class DataSet(val file: File) {
    // Which columns to use
    var independentColumn: String? = null
    var dependentColumn: String? = null

    // The main SMILE data frame
    var dataFrame: DataFrame? = null
        private set

    init {
        // Read the CSV file
        dataFrame = Read.csv(
            file.path,
            CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .get()
        )
    }

    /**
     * Gets the linear model of the dataSet based on the columns
     */
    fun getLinearModel(): LinearModel {
        return OLS.fit(Formula.of(dependentColumn, independentColumn), dataFrame)
    }

    /**
     * Gets the highest value of the dependent column
     */
    fun getHeight(): Double? {
        return dataFrame?.column(dependentColumn)?.toDoubleArray()?.max()
    }

    /**
     * Gets the highest value of the independent column
     */
    fun getWidth(): Double? {
        return dataFrame?.column(independentColumn)?.toDoubleArray()?.max()
    }
}