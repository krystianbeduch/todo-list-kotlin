package com.krybed.todolist.util.file

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.krybed.todolist.R
import com.krybed.todolist.data.db.AppDatabase
import com.krybed.todolist.data.model.Task
import com.krybed.todolist.data.model.enums.FileType
import com.krybed.todolist.data.model.enums.Priority
import com.krybed.todolist.util.converter.Converters
import com.krybed.todolist.util.file.json.TaskTypeJsonAdapter
import com.krybed.todolist.util.file.xml.TaskXml
import com.krybed.todolist.util.file.xml.TaskXmlWrapper
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.lang.reflect.Type
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors

object FileService {

    private val executor = Executors.newSingleThreadExecutor()

    fun exportTasksToCsv(ctx: Context) {
        writeToFile(ctx, FileType.CSV) { outputStream ->
            try {
                val db = AppDatabase.getInstance(ctx.applicationContext)
                val allTasks = db.taskDao().getAllSync()
                val csvBuilder = StringBuilder()
                csvBuilder.append(
                    ctx.getString(R.string.csv_header)
                ).append("\n")

                for (task in allTasks) {
                    csvBuilder.append(task.id).append(";")
                    csvBuilder.append(sanitize(task.title)).append(";")
                    csvBuilder.append(
                        Converters.fromLocalDateTimeToString(task.deadline)
                    ).append(";")
                    csvBuilder.append(task.priority).append(";")
                    csvBuilder.append(task.isDone).append(";")
                    csvBuilder.append(
                        Converters.fromLocalDateTimeToString(task.createdAt)
                    ).append("\n")
                }
                outputStream.write(
                    csvBuilder.toString().toByteArray(StandardCharsets.UTF_8)
                )
            }
            catch (e: Exception) {
                showToast(
                    ctx,
                    ctx.getString(R.string.error_export_csv) + " " + e.message
                )
                Log.e("Error CSV export", Log.getStackTraceString(e))
            }
        }
    }

    fun importTasksFromCsv(
        ctx: Context,
        fileUri: Uri
    ) {
        readFromFile(ctx, fileUri, FileType.CSV) { inputStream ->
            try {
                BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8)).use { reader ->
                    var line: String?
                    var isFirstLine = true
                    val db = AppDatabase.getInstance(ctx.applicationContext)

                    while (reader.readLine().also {
                        line = it
                    } != null) {
                        if (isFirstLine) {
                            isFirstLine = false
                            continue
                        }
                        val fields = line!!.split(";")
//                        val fields = line?.split(";")
                        if (fields.size < 6) {
                            continue
                        }

                        // TODO BUILDER
                        val task = Task()
                        task.title = fields[1]
                        task.deadline = Converters.fromStringToLocalDateTime(fields[2])
                        task.priority = Priority.valueOf(fields[3])
                        task.isDone = fields[4].toBoolean()
                        task.createdAt = Converters.fromStringToLocalDateTime(fields[5])
                        db.taskDao().insert(task)
                    }
                }
            }
            catch (e: Exception) {
                showToast(
                    ctx,
                    ctx.getString(R.string.error_import_csv) + " " + e.message
                )
                Log.e("Error CSV import", Log.getStackTraceString(e))
            }
        }
    }

    fun exportTasksToJson(ctx: Context) {
        writeToFile(ctx, FileType.JSON) { outputStream ->
            try {
                val db = AppDatabase.getInstance(ctx.applicationContext)
                val allTasks = db.taskDao().getAllSync()

                val gson = GsonBuilder()
                    .registerTypeAdapter(Task::class.java, TaskTypeJsonAdapter())
                    .setPrettyPrinting()
                    .create()
                val json = gson.toJson(allTasks)
                outputStream.write(json.toByteArray(StandardCharsets.UTF_8))
            }
            catch (e: Exception) {
                showToast(
                    ctx,
                    ctx.getString(R.string.error_export_json) + " " + e.message
                )
                Log.e("Error JSON export", Log.getStackTraceString(e))
            }
        }
    }

    fun importTasksFromJson(
        ctx: Context,
        fileUri: Uri
    ) {
        readFromFile(ctx, fileUri, FileType.JSON) { inputStream ->
            try {
                BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8)).use { reader ->
                    val gson = GsonBuilder()
                        .registerTypeAdapter(Task::class.java, TaskTypeJsonAdapter())
                        .create()

                    val taskListType: Type = object : TypeToken<List<Task>>(){}.type
                    val tasks: List<Task> = gson.fromJson(reader, taskListType)

                    val db = AppDatabase.getInstance(ctx.applicationContext)
                    for (task in tasks) {
                        db.taskDao().insert(task)
                    }
                }
            }
            catch (e: Exception) {
                showToast(
                    ctx,
                    ctx.getString(R.string.error_import_json) + " " + e.message
                )
                Log.e("Error JSON import", Log.getStackTraceString(e))
            }
        }
    }

    fun exportTasksToXml(ctx: Context) {
        writeToFile(ctx, FileType.XML) { outputStream ->
            try {
                val db = AppDatabase.getInstance(ctx.applicationContext)
                val allTasks = db.taskDao().getAllSync()

                val taskXmlList = allTasks.map { TaskXml(it) }
                val wrapper = TaskXmlWrapper(taskXmlList)
                val serializer: Serializer = Persister()
                serializer.write(wrapper, outputStream)
            }
            catch (e: Exception) {
                showToast(
                    ctx,
                    ctx.getString(R.string.error_export_xml) + " " + e.message
                )
                Log.e("Error XML export", Log.getStackTraceString(e))
            }
        }
    }

    fun importTasksFromXml(
        ctx: Context,
        fileUri: Uri
    ) {
        readFromFile(ctx, fileUri, FileType.XML) { inputStream ->
            try {
                val serializer: Serializer = Persister()
                val wrapper = serializer.read(TaskXmlWrapper::class.java, inputStream)
                val tasks = wrapper.taskXmlList.map { it.toTask() }

                val db = AppDatabase.getInstance(ctx.applicationContext)
                for (task in tasks) {
                    db.taskDao().insert(task)
                }
            }
            catch (e: Exception) {
                showToast(
                    ctx,
                    ctx.getString(R.string.error_import_xml) + " " + e.message
                )
                Log.e("Error XML import", Log.getStackTraceString(e))
            }
        }
    }

    fun copyFileToInternalStorage(
        ctx: Context,
        sourceUri: Uri,
        filename: String
    ): Uri? {
        val filenameWithoutExtension: String
        val extension: String
        val dotIndex = filename.lastIndexOf(".")

        if (dotIndex != -1) {
            filenameWithoutExtension = filename.substring(0, dotIndex)
            extension = filename.substring(dotIndex)
        }
        else {
            filenameWithoutExtension = filename
            extension = ""
        }

        val filenameWithTimestamp = "${filenameWithoutExtension}_${System.currentTimeMillis()}$extension"
        val targetFile = File(ctx.filesDir, filenameWithTimestamp)
        return try {
            ctx.contentResolver.openInputStream(sourceUri).use { inputStream ->
                FileOutputStream(targetFile).use { outputStream ->
                    if (inputStream == null) {
                        throw IOException(
                            ctx.getString(R.string.is_cannot_reconstructed)
                        )
                    }
                    val buffer = ByteArray(8192)
                    var length: Int
                    while (inputStream.read(buffer).also {
                        length = it
                    } != -1) {
                        outputStream.write(buffer, 0, length)
                    }
                    outputStream.flush()
                    FileProvider.getUriForFile(
                        ctx,
                        "com.krybed.todolist.fileprovider",
                        targetFile
                    )
                }
            }
        }
        catch (e: IOException) {
            Log.e("IOException: ", Log.getStackTraceString(e))
            null
        }


    }

    fun getFilenameFromUri(
        ctx: Context,
        uri: Uri
    ): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = ctx.contentResolver.query(
                uri, null, null, null, null
            )
            if (cursor != null && cursor.moveToFirst()) {
                result = cursor.getString(
                    cursor.getColumnIndexOrThrow(
                        OpenableColumns.DISPLAY_NAME
                    )
                )
                cursor.close()
            }
        }
        if (result == null) {
            result = uri.lastPathSegment
        }
        return result
    }

    fun getFilenameFromFilePicker(
        ctx: Context,
        uri: Uri
    ): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            ctx.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index >= 0) {
                        result = cursor.getString(index)
                    }
                }
            }
        }
        if (result == null) {
            result = uri.path
            result?.let {
                val cut = it.lastIndexOf('/')
                if (cut != -1) {
                    result = it.substring(cut + 1)
                }
            }
        }
        return result
    }

    fun isSupportedExtensionForFileType(
        fileType: FileType,
        filename: String?
    ): Boolean =
        filename != null && filename.lowercase().endsWith(
            fileType.extension
        )


    fun deleteFileFromInternalStorage(
        ctx: Context,
        filePath: String
    ): Boolean {
        return try {
            val fileUri = Uri.parse(filePath)
            val localFilePath = fileUri.path ?: return false

            if (!localFilePath.contains("/files")) {
                return false
            }

            val filename = localFilePath.substring(
                localFilePath.indexOf("/files/") +
                "/files/".length
            )
            val file = File(ctx.filesDir, filename)
            if (file.exists()) file.delete() else false
        }
        catch (e: Exception) {
            Log.e("Exception: ", Log.getStackTraceString(e))
            false
        }
    }

    @Throws(IOException::class)
    private fun openOutputStream(
        ctx: Context,
        filename: String,
        fileType: FileType
    ): OutputStream {
        val outputStream: OutputStream?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, filename)
                put(MediaStore.Downloads.MIME_TYPE, fileType.mimeType)
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val uri = MediaStore.Downloads.EXTERNAL_CONTENT_URI
            val fileUri = ctx.contentResolver.insert(uri, values)
                ?: throw IOException("Cannot create file ${fileType.name}")

            outputStream = ctx.contentResolver.openOutputStream(fileUri)
        }
        else {
            // Older android
            val path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            ).absolutePath
            val file = File(path, filename)
            outputStream = FileOutputStream(file)
        }
        return outputStream ?: throw IOException(
            "Unable to open stream for writing ${fileType.name}"
        )
    }

    @Throws(IOException::class)
    private fun openInputStream(
        ctx: Context,
        fileUri: Uri,
        fileType: FileType
    ): InputStream =
        ctx.contentResolver.openInputStream(fileUri)
            ?: throw IOException(
                "Failed to open file ${fileType.name}"
            )

    private fun writeToFile(
        ctx: Context,
        fileType: FileType,
        writer: (OutputStream) -> Unit
    ) {
        val filename = "task_export_${System.currentTimeMillis()}${fileType.extension}"
        executor.execute {
            try {
                openOutputStream(ctx, filename, fileType).use { outputStream ->
                    writer(outputStream)
                    showToast(
                        ctx,
                        ctx.getString(R.string.exported) + " " + fileType.name
                    )
                }
            }
            catch (e: Exception) {
                showToast(
                    ctx,
                    ctx.getString(R.string.export_error) + " " + fileType.name + ": " + e.message
                )
                Log.e("Error export ${fileType.name}", Log.getStackTraceString(e))
            }
        }
    }

    private fun readFromFile(
        ctx: Context,
        fileUri: Uri,
        fileType: FileType,
        reader: (InputStream) -> Unit
    ) {
        executor.execute {
            try {
                openInputStream(ctx, fileUri, fileType).use { inputStream ->
                    reader(inputStream)
                    showToast(ctx, "Import ${fileType.name}")
                }
            }
            catch (e: Exception) {
                showToast(
                    ctx,
                    ctx.getString(R.string.import_error) + " " + e.message
                )
                Log.e("Error import", Log.getStackTraceString(e))
            }
        }
    }

    private fun sanitize(input: String?): String {
        if (input == null) {
            return ""
        }
        var sanitized = input.replace("\"", "\"\"")
        if (sanitized.contains(";") || sanitized.contains("\n")) {
            sanitized = "\"$sanitized\""
        }
        return sanitized
    }

    private fun showToast(ctx: Context, msg: String) =
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show()
        }
}