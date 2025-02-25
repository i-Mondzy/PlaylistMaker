//class SettingsActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_settings)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//
//        val switch = findViewById<SwitchMaterial>(R.id.switch_theme)
//        switch.setOnCheckedChangeListener { switcher, checked ->
//            if (!checked) {
//                switch.background = ContextCompat.getDrawable(this, R.drawable.custom_ripple_night)
//            } else {
//                switch.background = ContextCompat.getDrawable(this, R.drawable.custom_ripple_day)
//            }
//
//            (applicationContext as App).switchTheme(checked)
//        }
//    }
//}
//
//
//const val LIGHT_DARK_THEME = "theme"
//const val THEME_KEY = "key_theme"
//
//class App : Application() {
//
//    var darkTheme = false
//
//    override fun onCreate() {
//        super.onCreate()
//
//        val sharedPrefs = getSharedPreferences(LIGHT_DARK_THEME, MODE_PRIVATE)
//
//        AppCompatDelegate.setDefaultNightMode(
//            if (sharedPrefs.getBoolean(THEME_KEY, darkTheme)) {
//                AppCompatDelegate.MODE_NIGHT_YES
//            } else {
//                AppCompatDelegate.MODE_NIGHT_NO
//            }
//        )
//
//    }
//
//    fun switchTheme(darkThemeEnabled: Boolean) {
//        darkTheme = darkThemeEnabled
//
//        val sharedPrefs = getSharedPreferences(LIGHT_DARK_THEME, MODE_PRIVATE)
//        sharedPrefs
//            .edit()
//            .putBoolean(THEME_KEY, darkTheme)
//            .apply()
//
//        AppCompatDelegate.setDefaultNightMode(
//            if (darkThemeEnabled) {
//                AppCompatDelegate.MODE_NIGHT_YES
//            } else {
//                AppCompatDelegate.MODE_NIGHT_NO
//            }
//        )
//    }
//}