import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.ksp)
    alias(libs.plugins.parcelize)
    alias(libs.plugins.googleAndroidLibrariesMapsplatformSecretsGradlePlugin)
}

android {
    namespace = "com.example.mystory"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mystory"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = Properties()

        val localPropertiesFile = project.rootProject.file("local.properties")

        properties.load(localPropertiesFile.inputStream())
        val baseUrl:String = properties.getProperty("BASE_URL")
        val mapAPI:String = properties.getProperty("MAP_API")

        buildConfigField(
            "String",
            "BASE_URL",
            "\"$baseUrl\""
        )

        buildConfigField(
            "String",
            "MAP_API",
            "\"$mapAPI\""
        )

        manifestPlaceholders["mapApi"] = mapAPI
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    testOptions {
        animationsDisabled = true
        unitTests {
            isReturnDefaultValues = true
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //lottie
    implementation(libs.lottie)

    //glide
    implementation(libs.glide)
    annotationProcessor(libs.glideCompiler)

    //retrofit + okhttp3
    implementation(libs.retrofit)
    implementation(libs.converterGson)
    implementation(libs.loggingInterceptor)

    //lifecycle
    implementation(libs.lifecycleRuntimeKtx)

    //coroutines
    implementation(libs.lifecycleViewModelKtx)
    implementation(libs.coroutinesCore)
    implementation(libs.lifecycleLivedataKtx)
    implementation(libs.coroutinesAndroid)

    //datastore
    implementation(libs.datastorePreferences)

    //room
    implementation(libs.roomKtx)
    implementation(libs.roomRuntime)
    ksp(libs.roomCompiler)

    //swipe refresh layout
    implementation(libs.swiperefreshlayout)

    //paging
    implementation(libs.pagingRuntimeKtx)
    implementation(libs.pagingCommonKtx)
    implementation(libs.roomPaging)

    //testing
    testImplementation(libs.coreTesting)
    testImplementation(libs.coroutinesTest)
    testImplementation(libs.mockitoCore)
    testImplementation(libs.mockitoInline)
    androidTestImplementation(libs.androidxTestExtJunit)
    androidTestImplementation(libs.mockwebserver)
    androidTestImplementation(libs.okhttpTls)
    implementation(libs.espressoIdlingResource)

    //map
    implementation(libs.play.services.maps)
    implementation(libs.playServicesLocation)
}