package hashtag.generator.hashapplication.Categories

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import hashtag.generator.hashapplication.ModelClasses.categoriesData
import com.project.hashapplication.R
import com.project.hashapplication.databinding.FragmentCategoryBinding
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [CategoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CategoryFragment : Fragment() {
    lateinit var binding: FragmentCategoryBinding
    lateinit var adaptor: CategoriesAdaptor
    lateinit var list_of_categories: ArrayList<categoriesData>
    var check_state = "a"
    lateinit var duplicateList: ArrayList<categoriesData>

    lateinit var bounce_animation: Animation


    // TODO: Rename and change types of parameters
    private var param1: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            param1 = requireArguments().getString(ARG_PARAM1)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        bounce_animation = AnimationUtils.loadAnimation(context, R.anim.bounce)
        // Inflate the layout for this fragment
        binding = FragmentCategoryBinding.inflate(inflater, container, false)
        try {
            val obj_categories_name = JSONObject(loadCategoriesJSONFromAsset())

            var categories_name_list = obj_categories_name.getJSONArray("Categories")
            var colors_list = resources.getStringArray(R.array.colors_array)
            var list_of_colors = java.util.ArrayList<String>()
            for (i in colors_list.indices) {
                list_of_colors.add(colors_list.get(i))
            }
            var k = 0
            var random_colors_list = java.util.ArrayList<String>()
            while (k < categories_name_list.length()) {
                var colorForCard = getRandomColorCard(list_of_colors)
                random_colors_list.add(colorForCard)
                k++
            }
            list_of_categories = ArrayList<categoriesData>()

            var i = 0
            while (i < categories_name_list.length()) {
                var categories_names = categories_name_list.getJSONObject(i)
                var name = categories_names.get("name")
                var img = categories_names.get("img")
                var color = random_colors_list.get(i)
                var drawable = getImageDrawable(img.toString())
                list_of_categories.add(categoriesData(name.toString(), drawable, color))
                Log.d("TAG color", "onCreateView: " + color.toString())
                i++
            }
            duplicateList = list_of_categories

            binding.recyclerViewCateories.layoutManager = GridLayoutManager(requireContext(), 2)
            adaptor = CategoriesAdaptor(requireContext(), list_of_categories)

            binding.recyclerViewCateories.adapter = adaptor
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        binding.searchId.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {
                adaptor.filter?.filter(s)
            }
        })

        binding.searchIcon.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                binding.searchIcon.startAnimation(bounce_animation)
                val imm: InputMethodManager? =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm!!.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

                binding.searchId.requestFocus()
                binding.searchId.isCursorVisible=true

                binding.titleCategory.visibility = View.GONE
                binding.sortCards.visibility = View.GONE
                binding.closeSearch.visibility = View.VISIBLE
                binding.cardSearch.visibility = View.VISIBLE
            }

        })

        binding.closeSearch.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {

                val imm: InputMethodManager? =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm!!.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0)

                binding.titleCategory.visibility = View.VISIBLE
                binding.sortCards.visibility = View.VISIBLE
                binding.closeSearch.visibility = View.GONE
                binding.cardSearch.visibility = View.GONE
                binding.searchId.text = null
            }

        })
        binding.sortCards.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                binding.sortCards.startAnimation(bounce_animation)
                if (!check_state.equals("a")) {
                    if (check_state.equals("b")) {  //checkstate=b
                        list_of_categories.sortWith { p0, p1 ->
                            p0.title_category.lowercase(Locale.getDefault()).compareTo(
                                p1.title_category.lowercase(Locale.getDefault())
                            )
                        }
                        binding.sortCards.rotationX = 180f
                        check_state = "c"
                    } else {  //checkstate=c
                        list_of_categories.sortWith { p0, p1 ->
                            p0.title_category.lowercase(Locale.getDefault()).compareTo(
                                p1.title_category.lowercase(Locale.getDefault())
                            )
                        }
                        binding.sortCards.rotationX = 0f
                        list_of_categories.reverse()
                        check_state = "a"
                    }
                } else {
                    Collections.shuffle(list_of_categories);
                    check_state = "b"
                }
                binding.recyclerViewCateories.layoutManager = GridLayoutManager(requireContext(), 2)
                adaptor = CategoriesAdaptor(requireContext(), list_of_categories)

                binding.recyclerViewCateories.adapter = adaptor
            }
        })
        return binding.root
    }

    fun getRandomColorCard(list_of_colors: java.util.ArrayList<String>): String {
        return list_of_colors.get(Random().nextInt(list_of_colors.size))
    }

    companion object {
        private val ARG_PARAM1 = "param1"

        @JvmStatic
        fun newInstance(param1: String) = CategoryFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, param1)
            }
        }
    }

    fun loadCategoriesJSONFromAsset(): String? {
        var json: String? = null
        try {
            val `is`: InputStream = requireContext().assets.open("categories_names.json")
            val size: Int = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            val charset: Charset = Charsets.UTF_8
            json = String(buffer, charset)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }


    fun getImageDrawable(img: String): Drawable {
        val folderName = "categories/"
        val fileName = img + ".png"
        Log.e("@@@@TAG", "getImageDrawable: " + fileName)
        val inputStream = requireContext()!!.assets.open(folderName + fileName)
        val d = Drawable.createFromStream(inputStream, null)
        inputStream.close()
        return d!!
    }

}