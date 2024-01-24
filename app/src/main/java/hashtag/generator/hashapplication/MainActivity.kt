package hashtag.generator.hashapplication

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.project.hashapplication.R
import hashtag.generator.hashapplication.Categories.CategoryFragment
import hashtag.generator.hashapplication.Favourites.FavouriteFragment
import hashtag.generator.hashapplication.Home.SomeFragment
import hashtag.generator.hashapplication.Saved.SaveFragment
import hashtag.generator.hashapplication.Trending.TrendingFragment
import com.project.hashapplication.databinding.ActivityMainBinding
import io.github.jitinsharma.bottomnavbar.model.NavObject
import io.paperdb.Paper


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private  val CAMERA_PERMISSION_CODE = 100
    private  val STORAGE_PERMISSION_CODE = 101

    var doubleBackToExitPressedOnce = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Paper.init(this);

//        checkPermission(
//            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            STORAGE_PERMISSION_CODE)


        showFragment("Home")

        binding.bottomBar.init(
            NavObject(
                image = ContextCompat.getDrawable(this, R.drawable.home_hash_svg)!!
            ), arrayListOf(
                NavObject(
                    name = "Category",
                    image = this.getDrawable(R.drawable.categories_svg)!!
                ),
                NavObject(
                    name = "Trending",
                    image = this.getDrawable(R.drawable.trending_svg)!!
                ),
                NavObject(
                    name = "Save",
                    image = this.getDrawable(R.drawable.save_svg)!!
                ),
                NavObject(
                    name = "Favourite",
                    image = this.getDrawable(R.drawable.favourites_svg)!!
                )
            )
        ) { position, primaryClicked ->
            when (position) {
                0 -> showFragment("Category")
                1 -> showFragment("Trending")
                2 -> showFragment("Save")
                3 -> showFragment("Favourite")
                else -> if (primaryClicked) showFragment("Home")
            }
        }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click back again to exit", Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            doubleBackToExitPressedOnce = false
        }, 2000)
    }

//    public fun checkPermission(permission: String, requestCode: Int) {
//        if (ContextCompat.checkSelfPermission(this@MainActivity, permission) == PackageManager.PERMISSION_DENIED) {
//
//            // Requesting the permission
//            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
//        } else {
//            Toast.makeText(this@MainActivity, "Permission already granted", Toast.LENGTH_SHORT).show()
//        }
//    }

//    override fun onRequestPermissionsResult(requestCode: Int,
//                                            permissions: Array<String>,
//                                            grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == CAMERA_PERMISSION_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this@MainActivity, "Camera Permission Granted", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(this@MainActivity, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
//            }
//        } else if (requestCode == STORAGE_PERMISSION_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this@MainActivity, "Storage Permission Granted", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(this@MainActivity, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

    fun showFragment(displayString: String) {
        lateinit var fragment: Fragment
        when (displayString) {
            "Category" -> {
                fragment = CategoryFragment.newInstance(displayString)
            }
            "Trending" -> {
                fragment = TrendingFragment.newInstance(displayString)
            }
            "Save" -> {
                fragment = SaveFragment.newInstance(displayString)
            }
            "Favourite" -> {
                fragment = FavouriteFragment.newInstance(displayString)
            }
            else -> fragment = SomeFragment.newInstance(displayString)
        }
        val transaction = supportFragmentManager.beginTransaction()
        transaction
            .replace(R.id.container, fragment)
            .commit()
    }
}