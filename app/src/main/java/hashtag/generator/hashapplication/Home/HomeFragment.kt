package hashtag.generator.hashapplication.Home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import co.lujun.androidtagview.TagView
import com.cunoraz.tagview.Tag
import com.cunoraz.tagview.TagView.OnTagClickListener
import com.project.hashapplication.R
import com.project.hashapplication.databinding.FragmentHomeBinding
import io.paperdb.Paper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.io.Serializable
import java.util.concurrent.Executors


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [SomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SomeFragment : Fragment(), Serializable {
    lateinit var binding: FragmentHomeBinding
    var check_recent_state: Boolean = true

    var mExecutorService = Executors.newSingleThreadExecutor()
    val handler = Handler(Looper.getMainLooper())

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = requireArguments().getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ):
            View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        var bounce_animation = AnimationUtils.loadAnimation(requireContext(), R.anim.bounce)

        binding.titleHome.text = setColorforTitle(binding.titleHome.text.toString(), 0, 1)
        binding.subTitleHome.text = setColorforTitle(binding.subTitleHome.text.toString(), 23, 27)

        binding.relativeSearchIcon.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                mExecutorService.execute {
                    binding.searchImage.startAnimation(bounce_animation)
                    showProgress()
                    var list_of_tags = ArrayList<String>()
                    var joinned_tag_list = ""
                    if (binding.textTag.tags.size != 0) {
                        var i = 0
                        while (i < binding.textTag.tags.size) {
                            joinned_tag_list += binding.textTag.tags.get(i).toString()
                            list_of_tags.add(binding.textTag.tags.get(i).mname.toString())
                            i++
                        }
                        if (!binding.searchId.text.isBlank() || !binding.searchId.text.isEmpty()) {
                            binding.textTag.addTag(binding.searchId.text.toString(), false)
                            joinned_tag_list += binding.searchId.text.toString()
                            list_of_tags.add(binding.searchId.text.toString())

                        }

                    } else {
                        if (!binding.searchId.text.isBlank() || !binding.searchId.text.isEmpty()) {
                            binding.textTag.addTag(binding.searchId.text.toString(), false)
                            list_of_tags.add(binding.searchId.text.toString())

//                            binding.scrollForRecyclerview.visibility=View.VISIBLE
                        } else {
//                            Toast.makeText(requireActivity(), "Please search a valid tag!!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    handler.post {
                        if ((binding.searchId.text.isEmpty() || binding.searchId.text.isBlank()) && (binding.textTag.tags.size.equals(0))) {
                            Toast.makeText(requireActivity(), "Tag can not be empty!", Toast.LENGTH_SHORT).show()
                        } else {
                            startActivity(
                                Intent(
                                    requireContext(),
                                    GenerateTagsActivity::class.java
                                ).putExtra("tagNameList", list_of_tags)
                                    .putExtra("joinned_tag", joinned_tag_list)
                            )
                        }
                    }
                }

            }
        })

        binding.textTag.layoutDirection = View.LAYOUT_DIRECTION_LOCALE
        binding.textTag.setOnTagClickListener(object : OnTagClickListener,
            TagView.OnTagClickListener {

            override fun onTagClick(position: Int, text: String?) {
            }

            override fun onTagLongClick(position: Int, text: String?) {
                TODO("Not yet implemented")
            }

            override fun onSelectedTagDrag(position: Int, text: String?) {
                TODO("Not yet implemented")
            }

            override fun onTagCrossClick(position: Int) {
                binding.textTag.removeTag(position)

                if (binding.textTag.tags.size.equals(0)) {
                    binding.scrollForRecyclerview.visibility = View.GONE
                }

            }

            override fun onTagstarClick(position: Int) {
            }

            override fun onTagClick(tag: Tag?, position: Int) {

            }
        })


        binding.showRecentId.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                if (check_recent_state) {
                    binding.recyclerViewRecent.visibility = View.VISIBLE
                    binding.showRecentId.rotationX = 180F
                    showRecentTags()
                    check_recent_state = false
                } else {
                    binding.showRecentId.rotationX = 0F
                    check_recent_state = true
                    binding.recyclerViewRecent.visibility = View.GONE
                }

            }
        })


        binding.relativeGenerateBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                binding.cardGenerate.startAnimation(bounce_animation)

                if (!binding.searchId.text.isEmpty() && !binding.searchId.text.isBlank()) {
                    binding.scrollForRecyclerview.visibility = View.VISIBLE
                    binding.textTag.addTag(binding.searchId.text.toString(), true)
                    binding.searchId.text.clear()
                    Toast.makeText(requireActivity(), "Tag has been generated!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(requireActivity(), "Tag cannot be empty!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })

        binding.cardClear.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                binding.cardClear.startAnimation(bounce_animation)
                binding.textTag.removeAllTags()
                binding.searchId.text = null
                binding.scrollForRecyclerview.visibility = View.GONE
            }

        })

        binding.searchId.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding.searchId.getText().toString().contains(" ")) {
                    var text = binding.searchId.getText().toString().trim()
                    if (!binding.searchId.text.isBlank()) {
                        binding.textTag.addTag(text, true)
                        binding.searchId.text = null
                        binding.scrollForRecyclerview.visibility = View.VISIBLE
                    }

                }

            }
        })
        return binding.root
    }

    private fun showProgress() {
        runBlocking(Dispatchers.Main) {
            binding.progressHome.visibility = View.VISIBLE
        }


    }

    private fun showRecentTags() {
        val tagsList: ArrayList<PredictedTagsAdaptor.RecentData>? = Paper.book().read("no_of_tags")

        if (tagsList != null) {
            binding.recyclerViewRecent.layoutManager =
                LinearLayoutManager(requireContext())

            binding.recyclerViewRecent.adapter = RecentTagsAdaptor(requireContext(), tagsList!!)
        } else {
            Toast.makeText(requireContext(), "list is empty", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private val ARG_PARAM1 = "param1"
        fun newInstance(param1: String): SomeFragment {
            val fragment = SomeFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            fragment.arguments = args
            return fragment
        }
    }

    fun setColorforTitle(text: String, start: Int, end: Int): SpannableString {
        val spannableString = SpannableString(text)
        var hashcolor = Color.parseColor("#FFA5A5")
        var txtcolor = Color.parseColor("#FFFFFFFF")
        val pink = ForegroundColorSpan(hashcolor)
        val white = ForegroundColorSpan(txtcolor)
        spannableString.setSpan(
            pink,
            start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }

}
