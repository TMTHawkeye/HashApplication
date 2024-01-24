package hashtag.generator.hashapplication.Home

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.hashapplication.databinding.ActivityGenerateTagsBinding
import kotlin.collections.ArrayList

class GenerateTagsActivity : AppCompatActivity(), java.io.Serializable {

    var tagArrayList = ArrayList<String>()
    lateinit var joinned_tags: String
    lateinit var binding: ActivityGenerateTagsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenerateTagsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        joinned_tags = intent.getStringExtra("joinned_tag")!!

        if (!joinned_tags.equals("empty") && !joinned_tags.equals("from_recent") && !joinned_tags.equals(
                "from_categories"
            )
        ) {
            tagArrayList = (intent.getSerializableExtra("tagNameList") as ArrayList<String>)
        } else {
            var tagMember = (intent.getStringExtra("tagNameList"))
            if (joinned_tags.equals("from_recent")) {
                tagMember = tagMember!!.substring(1)
            }
            tagArrayList = ArrayList<String>()
            tagArrayList.add(tagMember!!)
        }

        if (!joinned_tags.equals("from_categories") && !joinned_tags.equals("from_recent")) {
            binding.recyclerViewGenerated.layoutManager = LinearLayoutManager(this)
            binding.recyclerViewGenerated.adapter = PredictedTagsAdaptor(this, tagArrayList, "")
        } else {
            binding.recyclerViewGenerated.layoutManager = LinearLayoutManager(this)
            if (joinned_tags.equals("from_categories")) {
                binding.titleCategories.text=tagArrayList.get(0).toString()
                binding.recyclerViewGenerated.adapter =
                    PredictedTagsAdaptor(this, tagArrayList, "from_categories")
            } else {
                binding.recyclerViewGenerated.adapter =
                    PredictedTagsAdaptor(this, tagArrayList, "from_recent")
            }
        }

        binding.backFromGenerateActivity.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                finish()
            }

        })
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}