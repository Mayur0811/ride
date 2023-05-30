object AndroidConfig {
    const val kotlinVersion = "1.7.0"
    const val kotlinStdlibVersion = "1.6.21"
    const val minSdk = 21
    const val compileSdk = 32
    const val buildTools = "32.0.0"
    const val targetSdk = 32
}

object Dependencies {
    const val kotlinStandardLib =
        "org.jetbrains.kotlin:kotlin-stdlib:${AndroidConfig.kotlinStdlibVersion}"
    const val kotlinCoreKtx = "androidx.core:core-ktx:${AndroidConfig.kotlinVersion}"

    const val legacySupportV4 = "androidx.legacy:legacy-support-v4:1.0.0"

    private const val appCompat = "1.4.1"
    const val uiEssentialAppCompat = "androidx.appcompat:appcompat:$appCompat"

    private const val constraintLayout = "2.0.4"
    const val uiEssentialConstraintLayout =
        "androidx.constraintlayout:constraintlayout:$constraintLayout"

    private const val recycleView = "1.1.0"
    const val uiEssentialRecycleView = "androidx.recyclerview:recyclerview:$recycleView"

    private const val viewPager = "1.0.0"
    const val uiEssentialViewPager = "androidx.viewpager2:viewpager2:$viewPager"

    const val uiGlide = "com.github.bumptech.glide:glide:4.10.0"
    const val uiGlideProcessor = "com.github.bumptech.glide:compiler:4.10.0"

    private const val hiltAndroidVersion = "2.38.1"
    private const val hiltVersion = "1.0.0-alpha01"
    const val diHiltGradlePlugin =
        "com.google.dagger:hilt-android-gradle-plugin:$hiltAndroidVersion"
    const val diHiltAndroid = "com.google.dagger:hilt-android:$hiltAndroidVersion"
    const val diHiltAndroidCompiler = "com.google.dagger:hilt-android-compiler:$hiltAndroidVersion"
    const val diHiltLifecycle = "androidx.hilt:hilt-lifecycle-viewmodel:$hiltVersion"
    const val diHiltCompiler = "androidx.hilt:hilt-compiler:$hiltVersion"

    private const val navigationVersion = "2.4.2"
    const val uiNavigationFragment =
        "androidx.navigation:navigation-fragment-ktx:$navigationVersion"
    const val uiNavigationUI = "androidx.navigation:navigation-ui-ktx:$navigationVersion"

    private const val lifecycleVersion = "2.3.1"
    const val lifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion"
    const val lifecycleLiveData = "androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion"
    const val lifecycleRuntime = "androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion"
    const val lifecycleAnnotation = "androidx.lifecycle:lifecycle-compiler:$lifecycleVersion"

    private const val materialVersion = "1.6.0"
    const val uiMaterialSupport = "com.google.android.material:material:$materialVersion"

    const val reactiveRxJava3 = "io.reactivex.rxjava3:rxjava:3.0.7"
    const val reactiveRxAndroid = "io.reactivex.rxjava3:rxandroid:3.0.0"
    const val androidxSecurityCrypto = "androidx.security:security-crypto:1.1.0-alpha03"

    private const val pagingVersion = "3.0.1"
    const val pagingUI = "androidx.paging:paging-runtime:$pagingVersion"
    const val pagingRxJava = "androidx.paging:paging-rxjava3:$pagingVersion"

    private const val retrofitAndroidVersion = "2.9.0"
    private const val okhttpLoggingVersion = "4.9.0"
    const val networkRetrofit = "com.squareup.retrofit2:retrofit:$retrofitAndroidVersion"
    const val networkRetrofitJSONConvert =
        "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0"
    const val networkRetrofitLogging =
        "com.squareup.okhttp3:logging-interceptor:$okhttpLoggingVersion"
    const val adapterRxjava = "com.squareup.retrofit2:adapter-rxjava3:$retrofitAndroidVersion"
    const val jsonSerialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1"
    const val converter = "com.squareup.retrofit2:converter-gson:2.1.0"

    const val junit = "junit:junit:4.13.2"
    const val junitTest = "androidx.test.ext:junit:1.1.3"
    const val espresso = "androidx.test.espresso:espresso-core:3.4.0"
    const val signaturepad = "com.github.gcacace:signature-pad:1.3.1"

    const val googleMaps = "com.google.android.gms:play-services-maps:18.0.2"
    const val googlePlaces = "com.google.android.libraries.places:places:2.5.0"
    const val mapUtils = "com.google.maps.android:maps-utils-ktx:3.4.0"

    const val ratingBar = "me.zhanghai.android.materialratingbar:library:1.4.0"

    const val pinView = "com.github.mukeshsolanki:android-otpview-pinview:2.1.2"


    const val map = "com.google.maps:google-maps-services:0.9.3"
    const val circleImageview = "de.hdodenhof:circleimageview:3.1.0"
}