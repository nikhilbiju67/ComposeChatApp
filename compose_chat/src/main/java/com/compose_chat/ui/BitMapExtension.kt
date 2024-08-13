
import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

fun Bitmap.toFileConvert(context: Context, format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG): File {
    // Create a temporary file
    val file = File(context.cacheDir, "${System.currentTimeMillis()}.png")

    // Write the bitmap data to the file
    FileOutputStream(file).use { outputStream ->
        this.compress(format, 100, outputStream)
        outputStream.flush()
    }

    return file
}
