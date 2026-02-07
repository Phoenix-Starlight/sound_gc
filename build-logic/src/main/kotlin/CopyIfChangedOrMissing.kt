import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class CopyIfChangedOrMissing: DefaultTask() {
    @get:InputFile
    abstract val srcFile: RegularFileProperty

    @get:OutputFile
    abstract val outFile: RegularFileProperty

    init {
        outputs.upToDateWhen { outFile.get().asFile.exists() }
    }

    @TaskAction
    fun doCopy() {
        if (outFile.get().asFile.exists()) return
        srcFile.get().asFile.copyTo(outFile.get().asFile)
    }
}
