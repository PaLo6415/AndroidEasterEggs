plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.ksp) apply false
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}

task<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}

task<Exec>("changelogs") {
    // requirement python3
    workingDir("script/changelogs")
    commandLine("python3", "changelogs.py")
}
