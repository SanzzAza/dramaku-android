package com.dramaku.app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MimeTypes;
import androidx.media3.common.Player;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.exoplayer.DefaultRenderersFactory;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.ui.AspectRatioFrameLayout;
import androidx.media3.ui.PlayerView;

import java.util.Collections;
import java.util.Locale;

/** Full-screen player with quiet, custom controls instead of the stock Media3 chrome. */
public class PlayerActivity extends AppCompatActivity {
    public static final String EXTRA_URL = "url";
    public static final String EXTRA_SUBTITLE = "subtitle";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_DRAMA_ID = "dramaId";
    public static final String EXTRA_EPISODE = "episode";
    public static final String EXTRA_PLATFORM = "platform";
    public static final String EXTRA_START_POS = "startPos";
    public static final String RESULT_DRAMA_ID = "dramaId";
    public static final String RESULT_EPISODE = "episode";
    public static final String RESULT_PLATFORM = "platform";
    public static final String RESULT_POSITION = "position";
    public static final String RESULT_DURATION = "duration";
    public static final String RESULT_ENDED = "ended";

    private ExoPlayer player;
    private PlayerView playerView;
    private FrameLayout root;
    private View controls;
    private TextView playButton;
    private TextView timeLabel;
    private SeekBar seekBar;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean ended = false;
    private boolean resultSent = false;
    private String dramaId = "";
    private int episode = 1;
    private String platform = "";

    private final Runnable updateUi = new Runnable() {
        @Override public void run() {
            if (player != null) {
                long duration = Math.max(0, player.getDuration());
                long position = Math.max(0, player.getCurrentPosition());
                if (duration > 0 && !seekBar.isPressed()) seekBar.setProgress((int) ((position * 1000L) / duration));
                timeLabel.setText(formatTime(position) + "  /  " + formatTime(duration));
                playButton.setText(player.isPlaying() ? "Ⅱ" : "▶");
            }
            handler.postDelayed(this, 500);
        }
    };
    private final Runnable hideControls = new Runnable() { @Override public void run() { if (player != null && player.isPlaying()) controls.setVisibility(View.GONE); } };

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setImmersiveMode(true);

        root = new FrameLayout(this);
        root.setBackgroundColor(Color.BLACK);
        playerView = new PlayerView(this);
        playerView.setUseController(false);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        root.addView(playerView, new FrameLayout.LayoutParams(-1, -1));
        playerView.setOnClickListener(v -> toggleControls());
        buildControls();
        setContentView(root);
        root.setOnClickListener(v -> toggleControls());

        Intent in = getIntent();
        String url = in.getStringExtra(EXTRA_URL);
        String subtitle = in.getStringExtra(EXTRA_SUBTITLE);
        String title = in.getStringExtra(EXTRA_TITLE);
        dramaId = safe(in.getStringExtra(EXTRA_DRAMA_ID));
        platform = safe(in.getStringExtra(EXTRA_PLATFORM));
        episode = Math.max(1, in.getIntExtra(EXTRA_EPISODE, 1));
        long startPos = Math.max(0, in.getLongExtra(EXTRA_START_POS, 0L));
        if (url == null || url.trim().isEmpty()) { Toast.makeText(this, "URL video kosong", Toast.LENGTH_SHORT).show(); finishWithResult(false); return; }
        ((TextView) root.findViewWithTag("title")).setText(safe(title).isEmpty() ? "Dramaku" : title);
        ((TextView) root.findViewWithTag("episode")).setText("EPISODE " + episode);

        DefaultTrackSelector selector = new DefaultTrackSelector(this);
        selector.setParameters(selector.buildUponParameters().setPreferredVideoMimeTypes(MimeTypes.VIDEO_H264, MimeTypes.VIDEO_H265));
        DefaultHttpDataSource.Factory http = new DefaultHttpDataSource.Factory().setUserAgent("Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36 Chrome/121 Mobile Safari/537.36").setAllowCrossProtocolRedirects(true).setConnectTimeoutMs(15000).setReadTimeoutMs(30000);
        player = new ExoPlayer.Builder(this).setRenderersFactory(new DefaultRenderersFactory(this).setEnableDecoderFallback(true)).setTrackSelector(selector).setMediaSourceFactory(new DefaultMediaSourceFactory(http)).build();
        playerView.setPlayer(player);
        player.setMediaItem(buildMediaItem(url, subtitle));
        player.prepare();
        if (startPos > 0) player.seekTo(startPos);
        player.play();
        player.addListener(new Player.Listener() { @Override public void onPlaybackStateChanged(int state) { if (state == Player.STATE_ENDED) { ended = true; finishWithResult(true); } } });
        handler.post(updateUi);
        handler.postDelayed(hideControls, 4000);
    }

    private void buildControls() {
        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);
        top.setPadding(dp(18), dp(18), dp(18), dp(18));
        top.setBackground(topGradient(true));
        TextView back = text("‹", 38, Color.WHITE); back.setGravity(Gravity.CENTER); back.setOnClickListener(v -> finishWithResult(false));
        top.addView(back, new LinearLayout.LayoutParams(dp(48), dp(54)));
        LinearLayout heading = new LinearLayout(this); heading.setOrientation(LinearLayout.VERTICAL); heading.setPadding(dp(8), 0, 0, 0);
        TextView title = text("Dramaku", 16, Color.WHITE); title.setTag("title");
        TextView ep = text("EPISODE", 11, Color.rgb(188, 203, 219)); ep.setTag("episode");
        heading.addView(title); heading.addView(ep); top.addView(heading, new LinearLayout.LayoutParams(0, -2, 1));
        root.addView(top, frame(-1, dp(84), Gravity.TOP));

        LinearLayout bottom = new LinearLayout(this); bottom.setOrientation(LinearLayout.VERTICAL); bottom.setPadding(dp(20), dp(26), dp(20), dp(18)); bottom.setBackground(topGradient(false));
        timeLabel = text("00:00  /  00:00", 11, Color.rgb(209, 220, 231));
        seekBar = new SeekBar(this); seekBar.setMax(1000); seekBar.setProgress(0); seekBar.setPadding(0, 0, 0, 0);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { public void onStartTrackingTouch(SeekBar b) {} public void onStopTrackingTouch(SeekBar b) { if (player != null && player.getDuration() > 0) player.seekTo((player.getDuration() * b.getProgress()) / 1000L); } public void onProgressChanged(SeekBar b, int p, boolean fromUser) {} });
        bottom.addView(timeLabel); bottom.addView(seekBar, new LinearLayout.LayoutParams(-1, dp(36)));
        LinearLayout actions = new LinearLayout(this); actions.setGravity(Gravity.CENTER_VERTICAL); actions.setPadding(0, dp(6), 0, 0);
        TextView rewind = text("↶ 10", 14, Color.WHITE); rewind.setGravity(Gravity.CENTER); rewind.setOnClickListener(v -> seekBy(-10000));
        playButton = text("▶", 28, Color.WHITE); playButton.setGravity(Gravity.CENTER); playButton.setOnClickListener(v -> { if (player == null) return; if (player.isPlaying()) player.pause(); else player.play(); showControls(); });
        TextView forward = text("10 ↷", 14, Color.WHITE); forward.setGravity(Gravity.CENTER); forward.setOnClickListener(v -> seekBy(10000));
        actions.addView(rewind, new LinearLayout.LayoutParams(0, dp(58), 1)); actions.addView(playButton, new LinearLayout.LayoutParams(0, dp(58), 1)); actions.addView(forward, new LinearLayout.LayoutParams(0, dp(58), 1));
        bottom.addView(actions); controls = bottom; root.addView(bottom, frame(-1, dp(190), Gravity.BOTTOM));
    }

    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
    private FrameLayout.LayoutParams frame(int w, int h, int gravity) { FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(w, h); p.gravity = gravity; return p; }
    private TextView text(String value, int size, int color) { TextView v = new TextView(this); v.setText(value); v.setTextSize(size); v.setTextColor(color); v.setGravity(Gravity.CENTER_VERTICAL); return v; }
    private GradientDrawable topGradient(boolean top) { GradientDrawable g = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, top ? new int[]{0xDD07101D, 0x0007101D} : new int[]{0x0007101D, 0xF207101D}); return g; }
    private void seekBy(long ms) { if (player != null) player.seekTo(Math.max(0, Math.min(player.getDuration(), player.getCurrentPosition() + ms))); showControls(); }
    private void toggleControls() { if (controls.getVisibility() == View.VISIBLE) controls.setVisibility(View.GONE); else showControls(); }
    private void showControls() { controls.setVisibility(View.VISIBLE); handler.removeCallbacks(hideControls); handler.postDelayed(hideControls, 4000); }
    private String formatTime(long ms) { if (ms <= 0) return "00:00"; long sec = ms / 1000; return String.format(Locale.US, "%02d:%02d", sec / 60, sec % 60); }
    private String safe(String v) { return v == null ? "" : v; }
    private MediaItem buildMediaItem(String url, String subtitle) { MediaItem.Builder b = new MediaItem.Builder().setUri(Uri.parse(url)); if (url.toLowerCase().contains("m3u8")) b.setMimeType(MimeTypes.APPLICATION_M3U8); if (subtitle != null && !subtitle.trim().isEmpty()) { String mime = subtitle.toLowerCase().endsWith(".vtt") ? MimeTypes.TEXT_VTT : MimeTypes.APPLICATION_SUBRIP; b.setSubtitleConfigurations(Collections.singletonList(new MediaItem.SubtitleConfiguration.Builder(Uri.parse(subtitle)).setMimeType(mime).setLanguage("id").setSelectionFlags(C.SELECTION_FLAG_DEFAULT).build())); } return b.build(); }
    private void setImmersiveMode(boolean enabled) { getWindow().getDecorView().setSystemUiVisibility(enabled ? View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE : View.SYSTEM_UI_FLAG_LAYOUT_STABLE); }
    private void finishWithResult(boolean markEnded) { if (resultSent) { if (!isFinishing()) finish(); return; } resultSent = true; long pos = 0, dur = 0; try { if (player != null) { pos = Math.max(0, player.getCurrentPosition()); long d = player.getDuration(); dur = d > 0 ? d : 0; } } catch (Exception ignored) {} Intent data = new Intent(); data.putExtra(RESULT_DRAMA_ID, dramaId); data.putExtra(RESULT_EPISODE, episode); data.putExtra(RESULT_PLATFORM, platform); data.putExtra(RESULT_POSITION, pos); data.putExtra(RESULT_DURATION, dur); data.putExtra(RESULT_ENDED, markEnded || ended); setResult(RESULT_OK, data); if (!isFinishing()) finish(); }
    @Override public void onBackPressed() { finishWithResult(false); }
    @Override protected void onPause() { if (player != null) player.pause(); super.onPause(); }
    @Override protected void onDestroy() { handler.removeCallbacks(updateUi); handler.removeCallbacks(hideControls); if (!resultSent) finishWithResult(false); if (player != null) { try { player.release(); } catch (Exception ignored) {} player = null; } getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); super.onDestroy(); }
}
