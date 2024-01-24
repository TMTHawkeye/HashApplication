package hashtag.generator.hashapplication.Trending

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.hashapplication.databinding.FragmentTrendingBinding
import io.paperdb.Paper
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import kotlin.collections.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [TrendingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TrendingFragment : Fragment() {
    lateinit var binding: FragmentTrendingBinding

    // TODO: Rename and change types of parameters
    private var param1: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrendingBinding.inflate(inflater, container, false)

        //load tags from json
        val obj_categories = JSONObject(loadJSONFromAsset())
        //load categories names from json
        val obj_categories_name = JSONObject(loadCategoriesJSONFromAsset())
        //get json object from categories
        var categories_name_list = obj_categories_name.getJSONArray("Categories")

        //arraylist of names of categories , images excluded
        var final_categories_names = ArrayList<String>()
        for (i in 0 until categories_name_list.length()) {
            var each_object_of_list = categories_name_list.getJSONObject(i)
            var name = each_object_of_list.getString("name")
            final_categories_names.add(name)
        }
        // array list of all tags of all categories
        var final_all_tags_list = ArrayList<String>()
        for (i in 0 until final_categories_names.size) {
            var categories_tags_list = obj_categories.getJSONArray(final_categories_names.get(i))
            for (i in 0 until categories_tags_list.length()) {
                final_all_tags_list.add(categories_tags_list.get(i).toString())
            }
        }
        final_all_tags_list.shuffle()

        var favList = Paper.book().read("fav_tags", ArrayList<String>())


        for (i in 0 until 50) {
            if (favList!!.contains("#" + final_all_tags_list.get(i))) {
                binding.tagGroup.addTag("#" + final_all_tags_list.get(i), true)
            } else {
                binding.tagGroup.addTag("#" + final_all_tags_list.get(i), false)
            }
        }
//
        binding.tagGroup.setOnTagClickListener(object :
            co.lujun.androidtagview.TagView.OnTagClickListener {
            override fun onTagClick(position: Int, text: String?) {
            }

            override fun onTagLongClick(position: Int, text: String?) {
            }

            override fun onSelectedTagDrag(position: Int, text: String?) {
            }

            override fun onTagCrossClick(position: Int) {
                var favList = Paper.book().read("fav_tags", ArrayList<String>())
                if (favList!!.contains(binding.tagGroup.tags.get(position).mname)) {
                    binding.tagGroup.chdrawable(
                        position,
                        binding.tagGroup.tags.get(position).mname,
                        false
                    )
                    favList.remove(binding.tagGroup.tags.get(position).mname)
                    Paper.book().write("fav_tags", favList)
                } else {
                    binding.tagGroup.chdrawable(
                        position,
                        binding.tagGroup.tags.get(position).mname,
                        true
                    )
                    addToDB(binding.tagGroup.tags.get(position).mname)
                }
            }

            override fun onTagstarClick(position: Int) {

            }
        })
        // Inflate the layout for this fragment
        return binding.root
    }


    fun addToDB(path: String) {
        val pathDB = Paper.book().read("fav_tags", java.util.ArrayList<String>())
        val duplicateDB = java.util.ArrayList<String>()
        if (pathDB == null) {
            duplicateDB.add(path)
            Paper.book().write("fav_tags", duplicateDB)
        } else {
            if (pathDB.contains(path)) {
                pathDB.remove(path)
            }
            pathDB.add(path)
            Paper.book().write("fav_tags", pathDB)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) = TrendingFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, param1)
            }
        }
    }

    fun loadJSONFromAsset(): String? {
        var json: String? = null
        try {
            val `is`: InputStream = requireContext().assets.open("hashtags.json")
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


}