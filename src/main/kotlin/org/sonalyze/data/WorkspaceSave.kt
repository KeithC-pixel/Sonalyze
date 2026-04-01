package org.sonalyze.data

import java.io.File

/**
 * The WorkspaceSave is a singleton object that is used for saving DataSets and retrieving the
 * saved DataSet
 */
object WorkspaceSave {
    val homeDirectory: String? = System.getProperty("user.home")
    val recentFile = File("$homeDirectory/RecentSonalyzeWorkspace.workspace")

    /**
     * Saves a DataSet
     *
     * @param dataSet The DataSet to save3
     */
    fun saveRecentAs(dataSet: DataSet) {
        if (recentFile.isDirectory) return

        recentFile.writeText("""
            ${dataSet.file.path}
            ${dataSet.dependentColumn}
            ${dataSet.independentColumn}
        """.trimIndent())
    }

    /**
     * Loads the most recent save if saved
     *
     * @return null or valid data set with dependent and independent column selected
     */
    fun loadRecent(): DataSet? {
        if (!recentFile.exists() || !recentFile.isFile) return null
        val lines = recentFile.readLines()

        // Verify data set is still viable
        if (lines.size == 3) {
            val path = lines[0]
            val dependent = lines[1]
            val independent = lines[2]

            // Verify file
            val file = File(path)

            if (!file.exists() || !file.isFile)
                return null
            if (!file.name.endsWith(".csv", true))
                return null

            // Verify columns
            val dataSet = DataSet(file)
            val columnNames = dataSet.dataFrame?.names()!!

            if (!columnNames.contains(dependent) || !columnNames.contains((independent))) {
                return null
            }

            // Update DataSet
            dataSet.dependentColumn = dependent
            dataSet.independentColumn = independent

            return dataSet
        }
        return null
    }
}