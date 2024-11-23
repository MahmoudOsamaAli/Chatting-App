import androidx.recyclerview.widget.DiffUtil
import com.example.chatbox.data.FakeData

class MessagesDiffCallback(
    private val oldList: List<FakeData.Message>,
    private val newList: List<FakeData.Message>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }
    override fun getNewListSize(): Int {
        return newList.size
    }

    // Compare the identity of items (i.e., if they represent the same message)
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].name == newList[newItemPosition].name
    }

    // Compare the content of items (i.e., if their data has changed)
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
