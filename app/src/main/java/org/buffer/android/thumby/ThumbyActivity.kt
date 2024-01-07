package org.buffer.android.thumby

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_thumby.*
import org.buffer.android.thumby.listener.SeekListener

class ThumbyActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_THUMBNAIL_POSITION = "org.buffer.android.thumby.EXTRA_THUMBNAIL_POSITION"
        const val EXTRA_URI = "org.buffer.android.thumby.EXTRA_URI"

        fun getStartIntent(context: Context, uri: Uri, thumbnailPosition: Long = 0): Intent {
            val intent = Intent(context, ThumbyActivity::class.java)
            intent.putExtra(EXTRA_URI, uri)
            intent.putExtra(EXTRA_THUMBNAIL_POSITION, thumbnailPosition)
            return intent
        }
    }

    private lateinit var videoUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thumby)
        title = getString(R.string.picker_title)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        videoUri = (intent?.getParcelableExtra(EXTRA_URI) as? Uri)!!

        setupVideoContent()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.thumbnail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_menu_done -> {
                finishWithData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupVideoContent() {
        video_view.setVideoURI(videoUri)
        video_view.setOnPreparedListener {
            it.seekTo(0)
        }
        thumbs.seekListener = seekListener
        thumbs.currentSeekPosition = intent.getLongExtra(EXTRA_THUMBNAIL_POSITION, 0).toFloat()
        thumbs.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                thumbs.viewTreeObserver.removeOnGlobalLayoutListener(this)
                thumbs.uri = videoUri
            }
        })
    }

    private fun finishWithData() {
        val intent = Intent()
        intent.putExtra(
            EXTRA_THUMBNAIL_POSITION,
            ((video_view.duration / 100) * thumbs.currentProgress).toLong() * 1000
        )
        intent.putExtra(EXTRA_URI, videoUri)
        setResult(RESULT_OK, intent)
//        video_view.onPause()
        video_view.pause()
        thumbs.onPause()
        finish()
    }

    private val seekListener = object : SeekListener {
        override fun onVideoSeeked(percentage: Double) {
            video_view.seekTo((percentage.toInt() * video_view.duration) / 100)
        }
    }

   /* override fun onPause() {
        view_thumbnail.onPause()
        thumbs.onPause()
        super.onPause()
    }*/
}