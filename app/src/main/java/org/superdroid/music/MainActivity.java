package org.superdroid.music;

import android.app.*;
import android.content.*;
import android.database.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.provider.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import android.widget.AdapterView.*;
import java.io.*;
import java.text.*;
import java.util.*;

public class MainActivity extends Activity {
	
	ArrayAdapter<String> itemm;
	MediaPlayer mp;
	Notification noti;
	SeekBar pc, vc;
	AudioManager aud;
	String splitChar = "⃣⃣";
	int pold = 0;
	String[][] mlist;
	ListView lv;
	Toast t;
	boolean seekChange, stop, longclk, volChange = false;
	TextView topBar, nosong, tv, currentTime,
				songNumber, currentSong, artAlbum;
	RelativeLayout albumArtLayout, settingsView,
					albumArtLayoutBig, mainLayout;
	LinearLayout volume, controls, musicCtrl;
	ImageView vUp, vDown, hsb, playButton,
				prevButton, nextButton, albumArt, 
				albumArtBg, albumArtBig, hideShowAlbumArt;
	
	int padding = 38;
	int lvheight = 0;
	
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		t = Toast.makeText(this,"",Toast.LENGTH_SHORT);
		t.setView(toast());
		zdb = new ZooperDB("player_settings",getPackageName());
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		nosong = (TextView) findViewById(R.id.nosong);
		controls = (LinearLayout) findViewById(R.id.controls);
		volume = (LinearLayout) findViewById(R.id.volume);
		musicCtrl = (LinearLayout) findViewById(R.id.musicCtrl);
		topBar = (TextView) findViewById(R.id.topBar);
		settingsView = (RelativeLayout) findViewById(R.id.settingsView);
		vUp = (ImageView) findViewById(R.id.volumeUp);
		vDown = (ImageView) findViewById(R.id.volumeDown);
		pc = (SeekBar) findViewById(R.id.poscontrol);
		vc = (SeekBar) findViewById(R.id.volcontrol);
		aud = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		hsb = (ImageView) findViewById(R.id.hideShowList);
		lv = (ListView) findViewById(R.id.musicList);
		playButton = (ImageView) findViewById(R.id.play);
		prevButton = (ImageView) findViewById(R.id.prev);
		nextButton = (ImageView) findViewById(R.id.next);
		albumArt = (ImageView) findViewById(R.id.albumArt);
		albumArtBg = (ImageView) findViewById(R.id.albumArtBg);
		albumArtBig = (ImageView) findViewById(R.id.albumArtBig);
		albumArtLayout = (RelativeLayout) findViewById(R.id.albumArtLayout);
		hideShowAlbumArt = (ImageView) findViewById(R.id.hideShowAlbumArt);
		albumArtLayoutBig = (RelativeLayout) findViewById(R.id.albumArtLayoutBig);
		mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
		currentTime = (TextView) findViewById(R.id.currentTime);
		songNumber = (TextView) findViewById(R.id.songNumber);
		currentSong = (TextView) findViewById(R.id.song);
		artAlbum = (TextView) findViewById(R.id.artalbum);
		mlist = getmlist();
		if(Build.VERSION.SDK_INT >= 19){
			mainLayout.setPadding(0,getStatusBarHeight(),0,getNavigationBarHeight());
			RelativeLayout.LayoutParams rlp1 = 
				(RelativeLayout.LayoutParams)
					albumArtLayoutBig.getLayoutParams();
			RelativeLayout.LayoutParams rlp2 = 
				(RelativeLayout.LayoutParams)
					albumArtBg.getLayoutParams();
			rlp1.height = rlp1.height + getStatusBarHeight() + padding;
			rlp2.height = rlp2.height + getStatusBarHeight() + padding;
			albumArtLayoutBig.setLayoutParams(rlp1);
			albumArtBg.setLayoutParams(rlp2);
			albumArtLayoutBig.setPadding(padding,getStatusBarHeight()+padding,padding,padding);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		} else albumArtLayoutBig.setPadding(padding,padding,padding,padding);
		topBar.setPadding(padding,padding,0,0);
		vc.setMax(aud.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		vc.setProgress(aud.getStreamVolume(AudioManager.STREAM_MUSIC));
		vc.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
				@Override
				public void onProgressChanged(SeekBar p1, int p2, boolean p3){
					aud.setStreamVolume(AudioManager.STREAM_MUSIC,p2,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
				}

				@Override
				public void onStartTrackingTouch(SeekBar p1){
					volChange = true;
				}

				@Override
				public void onStopTrackingTouch(SeekBar p1){
					volChange = false;
				}
			});
			
		vUp.setOnClickListener(vUpClk);
		vDown.setOnClickListener(vDnClk);
		
		ImageView spUp = (ImageView) findViewById(R.id.spUp);
		ImageView spDn = (ImageView) findViewById(R.id.spDown);
		
		spUp.setOnClickListener(vUpClk);
		spDn.setOnClickListener(vDnClk);
		
		vUp.setLongClickable(true);
		vDown.setLongClickable(true);
		
		spUp.setLongClickable(true);
		spDn.setLongClickable(true);

		vUp.setOnLongClickListener(vUpLClk);
		vDown.setOnLongClickListener(vDnLClk);
		
		spUp.setOnLongClickListener(vUpLClk);
		spDn.setOnLongClickListener(vDnLClk);
			
		aud.requestAudioFocus(new AudioManager.OnAudioFocusChangeListener(){
				@Override
				public void onAudioFocusChange(int p1){
					if(!zdb.getBoolean("audfocus",false) && mp != null && mp.isPlaying()){
						switch(p1){
							case AudioManager.AUDIOFOCUS_GAIN:
								if(canduck){
									try {
										Thread.sleep(200);
									} catch(Exception e){}
									aud.setStreamVolume(AudioManager.STREAM_MUSIC,strvolume,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
									canduck = false;
								}
								break;
							case AudioManager.AUDIOFOCUS_LOSS:
							case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
								canduck = false;
								play(false);
								break;
							case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
								canduck = true;
								strvolume = aud.getStreamVolume(AudioManager.STREAM_MUSIC);
								aud.setStreamVolume(AudioManager.STREAM_MUSIC,1,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
								break;
							default:
								break;
						}
					}
				}
			},AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_REQUEST_GRANTED);
			
		
		hsb.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				if(lv.isShown()){
					lv.setVisibility(View.GONE);
					hsb.setImageDrawable(getResources().getDrawable(R.drawable.menu));
				} else {
					lv.setVisibility(View.VISIBLE);
					hsb.setImageDrawable(getResources().getDrawable(R.drawable.controls));
				}
			}
		});
		
		playButton.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v){
					if(!longclk) play();
					else longclk = false;
				}
			});
			
		playButton.setLongClickable(true);
		playButton.setOnLongClickListener(new View.OnLongClickListener(){
			@Override
			public boolean onLongClick(View p1){
				if(mp != null){
					mpStop();
					setNoti(pold,true);
					longclk = true;
				} return false;
			}
		});

		prevButton.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v){
					prev();
				}
			});

		nextButton.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v){
					next();
				}
			});
			
		hideShowAlbumArt.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				if(albumArtLayoutBig.isShown()){
					albumArtLayoutBig.setVisibility(View.GONE);
					hideShowAlbumArt.setImageDrawable(getResources().getDrawable(R.drawable.down));
					albumArtLayout.setVisibility(View.VISIBLE);
					if(Build.VERSION.SDK_INT >= 19){
						mainLayout.setPadding(0,getStatusBarHeight(),0,getNavigationBarHeight());
						topBar.setPadding(padding,padding,0,0);
					}
				} else {
					albumArtLayoutBig.setVisibility(View.VISIBLE);
					hideShowAlbumArt.setImageDrawable(getResources().getDrawable(R.drawable.up));
					albumArtLayout.setVisibility(View.GONE);
					if(Build.VERSION.SDK_INT >= 19){
						mainLayout.setPadding(0,0,0,getNavigationBarHeight());
						topBar.setPadding(padding,getStatusBarHeight()+padding,0,0);
					}
				}
			}
		});
		
		try{
			mediaButton();
			fillList(zdb.getBoolean("night",false));
			fillSettings();
			getLastSong();
			completed();
		} catch(Exception ignored){}
		
    }
	
	View.OnClickListener vUpClk = new View.OnClickListener(){
			public void onClick(View v){
				setVolumeBar(true);
			}
		};
		
	View.OnClickListener vDnClk = new View.OnClickListener(){
			public void onClick(View v){
				setVolumeBar(false);
			}
		};
		
	View.OnLongClickListener vUpLClk = new View.OnLongClickListener(){
		@Override
		public boolean onLongClick(View p1){
			aud.setStreamVolume(AudioManager.STREAM_MUSIC,aud.getStreamMaxVolume(AudioManager.STREAM_MUSIC),AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
			vc.setProgress(vc.getMax());
			return false;
		}
	};
	
	View.OnLongClickListener vDnLClk = new View.OnLongClickListener(){
		@Override
		public boolean onLongClick(View p1){
			aud.setStreamVolume(AudioManager.STREAM_MUSIC,0,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
			vc.setProgress(0);
			return false;
		}
	};
	
	int strvolume = 0;
	boolean canduck = false;
	
	void fillList(final boolean night){
		itemm = new ArrayAdapter<String>
		(this, android.R.layout.simple_list_item_1, android.R.id.text1){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				TextView text = (TextView) view.findViewById(android.R.id.text1);
				text.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
				text.setTextSize(16);
				text.setSingleLine();
				if(night)
					text.setTextColor(getResources().getColor(R.color.pixel_white));
				else text.setTextColor(getResources().getColor(R.color.pixel_dark));
				return view;
			}
		};
		if(night){
			lv.setBackgroundColor(getResources().getColor(R.color.pixel_dark));
			nosong.setTextColor(getResources().getColor(R.color.pixel_white));
		} else {
			lv.setBackgroundColor(getResources().getColor(R.color.pixel_white));
			nosong.setTextColor(getResources().getColor(R.color.pixel_dark));
		}
		lv.removeAllViewsInLayout();
		if(mlist[0][0] == "bos"){
			View[] x = {controls,volume,hsb,albumArtBg,albumArtLayout,albumArtLayoutBig,hideShowAlbumArt,musicCtrl,pc};
			for(View v : x) v.setVisibility(View.GONE);
			nosong.setVisibility(View.VISIBLE);
		} else {
			for(String s : mlist[0]) itemm.add(s);
			pc.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
					@Override
					public void onProgressChanged(SeekBar p1, int p2, boolean p3){}

					@Override
					public void onStartTrackingTouch(SeekBar p1){
						seekChange = true;
					}

					@Override
					public void onStopTrackingTouch(SeekBar p1){
						if(mp != null) mp.seekTo(p1.getProgress());
						seekChange = false;
					}
				});
			
			
			lv.setAdapter(itemm);
			registerForContextMenu(lv);
			lv.setOnItemClickListener(new OnItemClickListener(){
					@Override
					public void onItemClick(AdapterView<?> av, View v, final int p, long id){
						new Handler().postDelayed(new Runnable(){
							public void run(){
								try{
									if(mp != null && pold == p) play();
									else {
										if(mp != null && mp.isPlaying()){
											mpStop();
											stop = true;
										}
										pold = p;
										mp = MediaPlayer.create(MainActivity.this,Uri.parse(mlist[1][p]));
										completed();
										play();
										currentSong.setText(mlist[0][p]);
										artAlbum.setText(mlist[2][pold]+" - "+mlist[3][pold]);
										songNumber.setText((p+1)+"\n——\n"+mlist[0].length);
										setTxt();
									}
								} catch(Exception e){
									Toast.makeText(getBaseContext(),getResources().getString(R.string.muziksorunu),1000).show();
								}
							}
						},50);
						
					}
				});
		}
	}
	
	void setVolumeBar(boolean up){
		if(up){
			if(vc.getProgress() != aud.getStreamMaxVolume(AudioManager.STREAM_MUSIC))
				vc.setProgress(aud.getStreamVolume(AudioManager.STREAM_MUSIC)+1);
		} else {
			if(vc.getProgress() != 0)
				vc.setProgress(aud.getStreamVolume(AudioManager.STREAM_MUSIC)-1);
		} volChange = true;
	}
	
	void play(){
		if(mp.isPlaying()) play(false);
		else play(true);
	}
	
	void play(boolean play){
		try{
			if(mp != null){
				putLastSong();
				albumArt.setImageBitmap(getAlbumArt());
				albumArtBg.setImageBitmap(fastblur(getAlbumArt()));
				albumArtBig.setImageBitmap(getAlbumArt());
				if(!play){
					mp.pause();
					setNoti(pold,true);
				} else {
					mp.start();
					setNoti(pold,false);
				} 
			}
		} catch(Exception e){
			Toast.makeText(getBaseContext(),getResources().getString(R.string.muziksorunu),1000).show();
		}
	}
	
	void prev(){
		try{
			if(mp != null){
				if((mp.getCurrentPosition() >= ((mp.getDuration()/100)*2)) && mp.isPlaying()){
					mp.pause();
					mp.seekTo(0);
					mp.start();
				} else {
					if(mp.isPlaying())
						mpStop();
					if((pold-1) != -1){
						mp = MediaPlayer.create(MainActivity.this,Uri.parse(mlist[1][pold-1]));
						pold--;
					} else {
						mp = MediaPlayer.create(MainActivity.this,Uri.parse(mlist[1][0]));
						pold = 0;
					} 
					putLastSong();
					completed();
					currentSong.setText(mlist[0][pold]);
					artAlbum.setText(mlist[2][pold]+" - "+mlist[3][pold]);
					songNumber.setText((pold+1)+"\n——\n"+mlist[0].length);
					mp.start();
				}
				albumArt.setImageBitmap(getAlbumArt());
				albumArtBg.setImageBitmap(fastblur(getAlbumArt()));
				albumArtBig.setImageBitmap(getAlbumArt());
				setNoti(pold,false);
			}
		} catch(Exception e){
			Toast.makeText(getBaseContext(),getResources().getString(R.string.muziksorunu),1000).show();
		}
	}
	
	void next(){
		try{
			if(mp != null){
				if(mp.isPlaying()) mp.stop();
				if(rep != 3){
					if((pold+1) != mlist[1].length){
						mp = MediaPlayer.create(MainActivity.this,Uri.parse(mlist[1][pold+1]));
						pold++;
					} else {
						mp = MediaPlayer.create(MainActivity.this,Uri.parse(mlist[1][0]));
						pold = 0;
					}
				} else {
					pold = new Random().nextInt(mlist[0].length);
					mp = MediaPlayer.create(MainActivity.this,Uri.parse(mlist[1][pold]));
				}
				putLastSong();
				completed();
				albumArt.setImageBitmap(getAlbumArt());
				albumArtBg.setImageBitmap(fastblur(getAlbumArt()));
				albumArtBig.setImageBitmap(getAlbumArt());
				currentSong.setText(mlist[0][pold]);
				artAlbum.setText(mlist[2][pold]+" - "+mlist[3][pold]);
				songNumber.setText((pold+1)+"\n——\n"+mlist[0].length);
				mp.start();
				setNoti(pold,false);
			}
		} catch(Exception e){
			Toast.makeText(getBaseContext(),getResources().getString(R.string.muziksorunu),1000).show();
		}
	}
	
	public String PREV_RECV = "org.superdroid.music.PREV";
	public String PLAY_RECV = "org.superdroid.music.PLAY";
	public String PAUSE_RECV = "org.superdroid.music.PAUSE";
	public String NEXT_RECV = "org.superdroid.music.NEXT";

	NotificationManager mNotificationManager;

	public void setNoti(int p, boolean paused){
		Intent i1 = new Intent(PREV_RECV);
		Intent i2 = new Intent(PAUSE_RECV);
		Intent i3 = new Intent(NEXT_RECV);
		Intent i4 = new Intent(PLAY_RECV);
		PendingIntent pi1 = PendingIntent.getBroadcast(this,0,i1,0);
		PendingIntent pi2 = PendingIntent.getBroadcast(this,0,i2,0);
		PendingIntent pi3 = PendingIntent.getBroadcast(this,0,i3,0);
		PendingIntent pi4 = PendingIntent.getBroadcast(this,0,i4,0);
		PendingIntent pin = PendingIntent.getActivity(MainActivity.this,0,new Intent(MainActivity.this,MainActivity.class),0);
		Notification.Builder nb = new Notification.Builder(MainActivity.this)
			.setSmallIcon(R.drawable.stbn)
			.setContentTitle(mlist[0][p])
			.setContentText(mlist[2][p]+" - "+mlist[3][p])
			.setTicker(mlist[0][p]+" - "+mlist[2][p]+" - "+mlist[3][p])
			.setShowWhen(false)
			.setPriority(2147483647)
			.setContentIntent(pin);
		if(Build.VERSION.SDK_INT >= 23){
			if(rep != 2 && rep != 3)
				nb.addAction(new Notification.Action.Builder(android.R.drawable.ic_media_previous, getResources().getString(R.string.komut3), pi1).build());
			if(!paused)
				nb.addAction(new Notification.Action.Builder(android.R.drawable.ic_media_pause, getResources().getString(R.string.komut2), pi2).build());
			else nb.addAction(new Notification.Action.Builder(android.R.drawable.ic_media_play, getResources().getString(R.string.komut1), pi4).build());
			if(rep != 2)
				nb.addAction(new Notification.Action.Builder(android.R.drawable.ic_media_next, getResources().getString(R.string.komut4), pi3).build());
		} else {
			if(rep != 2 && rep != 3)
				nb.addAction(android.R.drawable.ic_media_previous,getResources().getString(R.string.komut3),pi1);
			if(!paused)
				nb.addAction(android.R.drawable.ic_media_pause,getResources().getString(R.string.komut2),pi2);
			else nb.addAction(android.R.drawable.ic_media_play,getResources().getString(R.string.komut1),pi4);
			if(rep != 2)
				nb.addAction(android.R.drawable.ic_media_next,getResources().getString(R.string.komut4),pi3);
		}
		if(Build.VERSION.SDK_INT >= 20) nb.setColor(getAppColor());
		noti = nb.build();
		if(!paused) noti.flags |= Notification.FLAG_NO_CLEAR;
		noti.flags |= Notification.FLAG_HIGH_PRIORITY;
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); 
		mNotificationManager.notify(0, noti);
	}
	
	void completed(){
		mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
				@Override
				public void onCompletion(MediaPlayer p1){
					switch(zdb.getInteger("repeat",0)){
						case 1:
							if(pold == mlist[0].length) pold = 0;
							next();
							completed();
							break;
						case 2:
							mpStop();
							play();
							break;
						case 3:
							pold = new Random().nextInt(mlist[0].length);
							next();
							completed();
							break;
						case 0:
						default:
							if((pold+1) != mlist[1].length){
								next();
								completed();
							}
						break;
					}
					
					setNoti(pold,false);
					currentSong.setText(mlist[0][pold]);
				}
			});
	}
	
	void mpStop(){
		putLastSong();
		mp.pause();
		mp.seekTo(0);
		pc.setProgress(0);
		currentTime.setText(getDuration(mp.getCurrentPosition()/1000)+"\n——\n"+getDuration(mp.getDuration()/1000));
	}
	
	boolean wired = false;
	boolean text = true;
	
	void setTxt(){
		if(!stop){
			new Handler().postDelayed(new Runnable(){
					public void run(){
						if(aud != null && mp != null){
							if(aud.isWiredHeadsetOn() != wired){
								if(!aud.isWiredHeadsetOn()){
									mp.pause();
									setNoti(pold,true);
								} if(!volChange){
									vc.setMax(aud.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
									vc.setProgress(aud.getStreamVolume(AudioManager.STREAM_MUSIC));
								} else volChange = false;
							} wired = aud.isWiredHeadsetOn();
							if(mp.isPlaying()){
								if(!seekChange){
									pc.setMax(mp.getDuration());
									pc.setProgress(mp.getCurrentPosition());
								} currentTime.setText(getDuration(mp.getCurrentPosition()/1000)+"\n——\n"+getDuration(mp.getDuration()/1000));
							}
						} setTxt();
					}
				},250);
		} else stop = false;
	}
	
	String[][] getmlist(){
		try{
			StringBuilder sb1 = new StringBuilder();
			StringBuilder sb2 = new StringBuilder();
			StringBuilder sb3 = new StringBuilder();
			StringBuilder sb4 = new StringBuilder();
			StringBuilder sb5 = new StringBuilder();
			StringBuilder sb6 = new StringBuilder();
			ContentResolver cr = getContentResolver();
			Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
			String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
			String sortOrder = MediaStore.Audio.Media.TITLE_KEY+ " ASC";
			Cursor cur = cr.query(uri, null, selection, null, sortOrder);
			int count = 0;
			if(cur != null){
				count = cur.getCount();
				if(count > 0){
					String temp1 = "";
					String temp2 = "";
					while(cur.moveToNext()){
						String title = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE));
						String data = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA));
						String album = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM));
						String artist = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST));
						int duration = cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.DURATION))/1000;
						int albumId = cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
						String[] fn = data.split("/");
						if(duration > 40 && !(fn[fn.length-1]).contains(".opus")){
							if(title.startsWith(" ") || title.endsWith(" ") || title.contains("\n")){
								temp1 += title;
								temp2 += data;
							} else {
								if(temp1.length() > 1){
									sb1.append(temp1+title);
									sb2.append(temp2+data);
									temp1 = "";
									temp2 = "";
								} else {
									sb1.append(title);
									sb2.append(data);
								}
								sb1.append(splitChar);
								sb2.append(splitChar);
								sb3.append(album+splitChar);
								sb4.append(artist+splitChar);
								sb5.append(getDuration(duration)+splitChar);
								sb6.append(albumId+splitChar);
							}
						}
					}
					sb1.replace(sb1.length()-splitChar.length(),sb1.length(),"");
					sb2.replace(sb2.length()-splitChar.length(),sb2.length(),"");
					sb3.replace(sb3.length()-splitChar.length(),sb3.length(),"");
					sb4.replace(sb4.length()-splitChar.length(),sb4.length(),"");
					sb5.replace(sb5.length()-splitChar.length(),sb5.length(),"");
					sb6.replace(sb6.length()-splitChar.length(),sb6.length(),"");
					String[] s1 = sb1.toString().split(splitChar);
					String[] s2 = sb2.toString().split(splitChar);
					String[] s3 = sb3.toString().split(splitChar);
					String[] s4 = sb4.toString().split(splitChar);
					String[] s5 = sb5.toString().split(splitChar);
					String[] s6 = sb6.toString().split(splitChar);
					String[][] sa = {s1,s2,s3,s4,s5,s6};
					return sa;
				}
			}
			cur.close();
			return new String[][]{{"bos"},{"bos"}};
		} catch(Exception e){
			Toast.makeText(getBaseContext(),e.toString(),Toast.LENGTH_LONG).show();
			return new String[][]{{"bos"},{"bos"}};
		}
	}
	
	String getDuration(long duration){
		String temp = "";
		long sa = (duration/60)/60;
		long dk = (duration/60)%60;
		long sn = duration%60;
		if(sa > 0) temp += zeroFix(sa)+":";
		temp += zeroFix(dk)+":";
		temp += zeroFix(sn);
		return temp;
	}
	
	String zeroFix(long number){
		if(number < 10) return "0"+number;
		return ""+number;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode == event.KEYCODE_VOLUME_DOWN) setVolumeBar(false);
		else if(keyCode == event.KEYCODE_VOLUME_UP) setVolumeBar(true);
		else if(keyCode == event.KEYCODE_HEADSETHOOK) play();
		else if(keyCode == event.KEYCODE_BACK){
			if(!lv.isShown()) lv.setVisibility(View.VISIBLE);
			else {
				ad = new AlertDialog.Builder(this)
					.setNegativeButton(getResources().getString(android.R.string.cancel), new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface p1, int p2){
							ad.cancel();
						};
					})
					.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface p1, int p2){
							mp.stop();
							finish();
						}
					}).setView(messageBoxView(getResources().getString(R.string.uygulamacik))).create();
				ad.requestWindowFeature(Window.FEATURE_NO_TITLE);
				if(Build.VERSION.SDK_INT > 19)
					ad.getWindow().setBackgroundDrawable(setDrawableFromColor());
				ad.show();
			}
		} return !lv.isShown();
	}

	@Override
	protected void onResume(){
		if(vc != null && aud != null)
			vc.setProgress(aud.getStreamVolume(AudioManager.STREAM_MUSIC));
		super.onResume();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
		menu.add(Menu.NONE, 0, Menu.NONE, getResources().getString(R.string.komut5));
		menu.add(Menu.NONE, 1, Menu.NONE, getResources().getString(R.string.komut6));
		menu.add(Menu.NONE, 2, Menu.NONE, getResources().getString(R.string.komut7));
	}
	
	int ii;
	
	@Override
	public boolean onContextItemSelected(MenuItem item){
		AdapterView.AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();
		ii = (int) lv.getItemIdAtPosition(acmi.position);
		switch(item.getItemId()){
			case 0:
				delete(mlist[1][ii]);
				break;
			case 1:
				share(mlist[1][ii]);
				break;
			case 2:
				options(mlist[1][ii]);
				break;
			default:
				break;
		} return super.onContextItemSelected(item);
	}
	
	AlertDialog ad;
	
	String x = "";
	String y = "";
	
	void delete(String fileName){
		StringBuilder sb = new StringBuilder();
		String[] fn = fileName.split("/");
		for(int i = 0;i != fn.length-1;i++)
			sb.append(fn[i]+"/");
		x = sb.toString();
		y = fn[fn.length-1];
		ad = new AlertDialog.Builder(this)
			.setNegativeButton(getResources().getString(android.R.string.cancel), new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface p1, int p2){
					ad.cancel();
				};
			})
			.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface p1, int p2){
					new File(x+y).delete();
					fillList(zdb.getBoolean("night",false));
				}
			}).setView(messageBoxView(String.format(getResources().getString(R.string.silmedialog),y))).create();
		ad.requestWindowFeature(Window.FEATURE_NO_TITLE);
		if(Build.VERSION.SDK_INT > 19)
			ad.getWindow().setBackgroundDrawable(setDrawableFromColor());
		ad.show();
	}
	
	void share(String fileName){
		String[] fn = fileName.split("/");
		y = fn[fn.length-1];
		String z = y.substring(y.lastIndexOf("."));
		z = z.replaceFirst(".","");
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND); 
		sharingIntent.setType(MimeTypeMap.getSingleton().getMimeTypeFromExtension(z));
		sharingIntent.putExtra(android.content.Intent.EXTRA_STREAM, Uri.fromFile(new File(fileName))/*shareBody*/);
		startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.komut6)));
	}
	
	void options(String fileName){
		ad = new AlertDialog.Builder(this)
			.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface p1, int p2){ ad.cancel(); }
			}).setView(messageBoxView(getResources().getString(R.string.medyabilgi1)+fileName+
						"\n\n"+getResources().getString(R.string.medyabilgi2)+mlist[4][ii]+
						"\n\n"+getResources().getString(R.string.medyabilgi3)+getFileSize(fileName)+
						"\n\n"+getResources().getString(R.string.medyabilgi4)+mlist[2][ii]+
						"\n\n"+getResources().getString(R.string.medyabilgi5)+mlist[3][ii])).create();
		ad.requestWindowFeature(Window.FEATURE_NO_TITLE);
		if(Build.VERSION.SDK_INT > 19)
			ad.getWindow().setBackgroundDrawable(setDrawableFromColor());
		ad.show();
	}
	
	LinearLayout messageBoxView(String message){
		LinearLayout ll = new LinearLayout(this);
		ll.setLayoutParams(new LinearLayout.LayoutParams(
							   LinearLayout.LayoutParams.MATCH_PARENT,
							   LinearLayout.LayoutParams.MATCH_PARENT));
		ll.setPadding(32,32,32,32);
		ll.setGravity(Gravity.CENTER);
		TextView tv = new TextView(this);
		tv.setLayoutParams(new LinearLayout.LayoutParams(
							   LinearLayout.LayoutParams.WRAP_CONTENT,
							   LinearLayout.LayoutParams.WRAP_CONTENT));
		tv.setTypeface(Typeface.create("sans-serif-light",Typeface.NORMAL));
		if(Build.VERSION.SDK_INT > 19)
			tv.setTextColor(getResources().getColor(R.color.pixel_white));
		tv.setTextSize(18);
		tv.setText(message);
		ll.addView(tv);
		return ll;
	}
	
	String getFileSize(String fileName){
		float temp = new File(fileName).length();
		float i1 = temp/1024f;
		float i2 = i1/1024f;
		float i3 = i2/1024f;
		float i4 = i3/1024f;
		float i5 = i4/1024f;
		if((int) i5 != 0)
			return new DecimalFormat("##########.##").format(i5)+"PB";
		else if((int) i4 != 0)
			return new DecimalFormat("##########.##").format(i4)+"TB";
		else if((int) i3 != 0)
			return new DecimalFormat("##########.##").format(i3)+"GB";
		else if((int) i2 != 0)
			return new DecimalFormat("##########.##").format(i2)+"MB";
		return new DecimalFormat("##########.##").format(i1)+"KB";
	}
	
	void mediaButton(){
		IntentFilter f1 = new IntentFilter(PREV_RECV);
		IntentFilter f2 = new IntentFilter(PAUSE_RECV);
		IntentFilter f3 = new IntentFilter(NEXT_RECV);
		IntentFilter f4 = new IntentFilter(PLAY_RECV);
		IntentFilter f5 = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
		IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
		f1.setPriority((int)Math.pow(2,32));
		f2.setPriority((int)Math.pow(2,32));
		f3.setPriority((int)Math.pow(2,32));
		f4.setPriority((int)Math.pow(2,32));
		f5.setPriority((int)Math.pow(2,32));
		filter.setPriority((int)Math.pow(2,32));
		if(rep != 2 && rep != 3)
			registerReceiver(new StatusBarRecv(), f1);
		registerReceiver(new StatusBarRecv(), f2);
		if(rep != 2)
			registerReceiver(new StatusBarRecv(), f3);
		registerReceiver(new StatusBarRecv(), f4);
		registerReceiver(new BecomingNoisyReceiver(), f5);
		registerReceiver(new MediaButtonIntentReceiver(), filter);
	}
	
	public class MediaButtonIntentReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent){
			String intentAction = intent.getAction();
			if (!Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) return;
			KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
			if (event == null) return;
			int action = event.getAction();
			int code = event.getKeyCode();
			if (action == KeyEvent.ACTION_DOWN){
				switch(code){
					case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
					case KeyEvent.KEYCODE_HEADSETHOOK:
						play();
						break;
					case KeyEvent.KEYCODE_MEDIA_NEXT:
						if(rep != 2)
							next();
						break;
					case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
						if(rep != 2 && rep != 3)
							prev();
						break;
					case KeyEvent.KEYCODE_MEDIA_EJECT:
						mpStop();
						break;
					case KeyEvent.KEYCODE_MEDIA_PLAY:
						play(true);
						break;
					case KeyEvent.KEYCODE_MEDIA_PAUSE:
						play(false);
						break;
					default:
						break;
				}
			} abortBroadcast();
		}
	}
	
	void setBgColor(int bgColor,int bgColorDark, View... item){
		for(View v : item)
			v.setBackgroundColor(bgColor);
		settingsView.setBackgroundColor(bgColorDark);
		getWindow().setBackgroundDrawable(setDrawableFromColor());
		if(mp != null && mp.isPlaying())
			setNoti(pold,false);
	}
	
	ListView settings;
	ZooperDB zdb;
	boolean start = true;
	int theme;
	int rep;
	
	void fillSettings(){
		if(start){
			zdb.putInteger("sleep",0);
			zdb.refresh();
			rep = zdb.getInteger("repeat",0);
			start = false;
		}
		
		if(rep == 2 || rep == 3) prevButton.setVisibility(View.INVISIBLE);
		else prevButton.setVisibility(View.VISIBLE);
		if(rep == 2) nextButton.setVisibility(View.INVISIBLE);
		else nextButton.setVisibility(View.VISIBLE);
		
		Switch nightMode = (Switch) findViewById(R.id.nightMode);
		Switch audFocus = (Switch) findViewById(R.id.audFocus);
		
		final int themeMax = 8;
		final int repeatMax = 4;
		
		final int[] minutes = { 5,10,15,30,45,60,75,90,120 };
		
		settings = (ListView) findViewById(R.id.settingsList);
		theme = zdb.getInteger("theme",0);
		nightMode.setChecked(zdb.getBoolean("night",false));
		nightMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton p1, boolean p2){
				zdb.putBoolean("night",p2);
				zdb.refresh();
				fillList(p2);
				String s = getResources().getString(R.string.gecemodu1);
				if(p2) s += getResources().getString(R.string.gecemodu2);
				else s += getResources().getString(R.string.gecemodu3);;
				showToast(s);
				fillSettings();
			}
		});
		audFocus.setChecked(zdb.getBoolean("audfocus",false));
		audFocus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton p1, boolean p2){
				zdb.putBoolean("audfocus",p2);
				zdb.refresh();
				String s = getResources().getString(R.string.audiofocus2);
				if(p2) s += getResources().getString(R.string.audiofocus3);
				else s += getResources().getString(R.string.audiofocus4);
				showToast(s);
			}
		});
		
		String tema = getResources().getString(R.string.secilitema1)+getStr(R.string.secilitema2,theme);
		
		setAppTheme(theme);
		item = new ArrayAdapter<String>
		(this, android.R.layout.simple_list_item_1, android.R.id.text1){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				TextView text = (TextView) view.findViewById(android.R.id.text1);
				text.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
				text.setTextSize(16);
				text.setTextColor(getResources().getColor(R.color.pixel_white));
				return view;
			}
		};
		settings.removeAllViewsInLayout();
		item.add(tema);
		String sleepTimer = getResources().getString(R.string.uykuzamanl1);
		switch(zdb.getInteger("sleep",0)){
			case 0:
				sleepTimer+=getResources().getString(R.string.uykuzamanl2);
				break;
			default:
				sleepTimer+=minutes[zdb.getInteger("sleep",0)-1]+getResources().getString(R.string.uykuzamanl3);
		}
		item.add(sleepTimer);
		String repMode = getResources().getString(R.string.calmamodu1)+getStr(R.string.calmamodu2,rep);
		item.add(repMode);
		item.add(getResources().getString(R.string.medyatara1));
		settings.setAdapter(item);
		settings.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4){
				zdb.putInteger("mode",p3);
				zdb.refresh();
				ArrayAdapter<String> items = new ArrayAdapter<String>
				(MainActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1){
					@Override
					public View getView(int position, View convertView, ViewGroup parent) {
						View view = super.getView(position, convertView, parent);
						TextView text = (TextView) view.findViewById(android.R.id.text1);
						text.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
						text.setTextSize(16);
						if(Build.VERSION.SDK_INT > 19)
							text.setTextColor(getResources().getColor(R.color.pixel_white));
						return view;
					}
				};
				
				if(p3 == 0){
					try {
						for(int i = 0;i != themeMax;i++)
							items.add(getStr(R.string.secilitema2,i));
					} catch(Exception e){}
				} else if(p3 == 1){
					items.add(getResources().getString(R.string.uykuzamanl2));
					for(int i = 0;i != minutes.length;i++)
						items.add(minutes[i]+getResources().getString(R.string.uykuzamanl3));
				} else if(p3 == 2){
					try {
						for(int i = 0;i != repeatMax;i++)
							items.add(getStr(R.string.calmamodu2,i));
					} catch(Exception e){}
				}
				
				ListView lv1 = new ListView(MainActivity.this);
				lv1.setLayoutParams(new ListView.LayoutParams(
										ListView.LayoutParams.MATCH_PARENT,
										ListView.LayoutParams.WRAP_CONTENT));
				ColorDrawable cd = new ColorDrawable();
				cd.setColor(getResources().getColor(android.R.color.transparent));
				lv1.setDivider(cd);
				lv1.setAdapter(items);
				lv1.setPadding(32,32,32,32);
				if(p3 == 0)
					lv1.setOnItemClickListener(new AdapterView.OnItemClickListener(){
							@Override
							public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4){
								ad.dismiss();
								zdb.putInteger("theme",p3);
								zdb.refresh();
								setAppTheme(p3);
								showToast(getStr(R.string.secilitema2,zdb.getInteger("theme",0))+getResources().getString(R.string.secilitema0));
								fillSettings();
							}
						});
				else if(p3 == 1)
					lv1.setOnItemClickListener(new AdapterView.OnItemClickListener(){
							@Override
							public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4){
								ad.dismiss();
								zdb.putInteger("sleep",p3);
								zdb.refresh();
								fillSettings();
								if(p3 > 0){
									showToast(String.format(getResources().getString(R.string.uykuzamanl5),""+minutes[p3-1]));
									repeat = 0;
									sleepTimer();
								} else showToast(getResources().getString(R.string.uykuzamanl4));
							}
						});
				else if(p3 == 2)
					lv1.setOnItemClickListener(new AdapterView.OnItemClickListener(){
							@Override
							public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4){
								ad.dismiss();
								zdb.putInteger("repeat",p3);
								zdb.refresh();
								showToast(getResources().getString(R.string.calmamodu6)+getStr(R.string.calmamodu2,p3));
								rep = p3;
								fillSettings();
							}
						});
				else if(p3 == 3) scan();
				if(p3 != 3){
					ad = new AlertDialog.Builder(MainActivity.this)
						.setPositiveButton(getResources().getString(android.R.string.cancel), new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface p1, int p2){
								ad.dismiss();
							}
						}).setView(lv1).create();
					ad.requestWindowFeature(Window.FEATURE_NO_TITLE);
					if(Build.VERSION.SDK_INT > 19)
						ad.getWindow().setBackgroundDrawable(setDrawableFromColor());
					ad.show();
				}
			}
		});
	}
	
	ArrayAdapter<String> item;
	
	public Drawable setDrawableFromColor(){
		float radius = 16f;
		GradientDrawable gd = new GradientDrawable();
		gd.setOrientation(GradientDrawable.Orientation.BOTTOM_TOP);
		gd.setColors(new int[]{getAppColor(),getAppColor()});
		gd.setCornerRadii(new float[]{radius,radius,radius,radius,radius,radius,radius,radius});
		return gd;
	}
	
	int getAppColor(){
		switch(zdb.getInteger("theme",0)){
			case 1:
				return getResources().getColor(R.color.pixel_yellow_dark);
			case 2:
				return getResources().getColor(R.color.pixel_green_dark);
			case 3:
				return getResources().getColor(R.color.pixel_red_dark);
			case 4:
				return getResources().getColor(R.color.pixel_purple_dark);
			case 5:
				return getResources().getColor(R.color.pixel_pink_dark);
			case 6:
				return getResources().getColor(R.color.pixel_turquoise_dark);
			case 7:
				return getResources().getColor(R.color.pixel_darker_gray);
			case 0:
			default:
				return getResources().getColor(R.color.pixel_blue_dark);
		}
	}
	
	void setAppTheme(int theme){
		switch(theme){
			case 0:
				setBgColor(getResources().getColor(R.color.pixel_blue),
						   getResources().getColor(R.color.pixel_blue_dark),
						   hsb,controls,hideShowAlbumArt,albumArtBg
						   ,musicCtrl,pc);
				break;
			case 1:
				setBgColor(getResources().getColor(R.color.pixel_yellow),
						   getResources().getColor(R.color.pixel_yellow_dark),
						   hsb,controls,hideShowAlbumArt,albumArtBg
						   ,musicCtrl,pc);
				break;
			case 2:
				setBgColor(getResources().getColor(R.color.pixel_green),
						   getResources().getColor(R.color.pixel_green_dark),
						   hsb,controls,hideShowAlbumArt,albumArtBg
						   ,musicCtrl,pc);
				break;
			case 3:
				setBgColor(getResources().getColor(R.color.pixel_red),
						   getResources().getColor(R.color.pixel_red_dark),
						   hsb,controls,hideShowAlbumArt,albumArtBg
						   ,musicCtrl,pc);
				break;
			case 4:
				setBgColor(getResources().getColor(R.color.pixel_purple),
						   getResources().getColor(R.color.pixel_purple_dark),
						   hsb,controls,hideShowAlbumArt,albumArtBg
						   ,musicCtrl,pc);
				break;
			case 5:
				setBgColor(getResources().getColor(R.color.pixel_pink),
						   getResources().getColor(R.color.pixel_pink_dark),
						   hsb,controls,hideShowAlbumArt,albumArtBg
						   ,musicCtrl,pc);
				break;
			case 6:
				setBgColor(getResources().getColor(R.color.pixel_turquoise),
						   getResources().getColor(R.color.pixel_turquoise_dark),
						   hsb,controls,hideShowAlbumArt,albumArtBg
						   ,musicCtrl,pc);
				break;
			case 7:
				setBgColor(getResources().getColor(R.color.pixel_gray),
						   getResources().getColor(R.color.pixel_darker_gray),
						   hsb,controls,hideShowAlbumArt,albumArtBg
						   ,musicCtrl,pc);
				break;
			default:
				break;
		}
	}
	
	void getLastSong(){
		pold = zdb.getInteger("lastSong",pold);
		mp = MediaPlayer.create(this,Uri.parse(mlist[1][pold]));
		setTxt();
		albumArt.setImageBitmap(getAlbumArt());
		albumArtBg.setImageBitmap(fastblur(getAlbumArt()));
		albumArtBig.setImageBitmap(getAlbumArt());
		currentSong.setText(mlist[0][pold]);
		currentTime.setText(getDuration(mp.getCurrentPosition()/1000)+"\n——\n"+getDuration(mp.getDuration()/1000));
		songNumber.setText((pold+1)+"\n——\n"+mlist[0].length);
		artAlbum.setText(mlist[2][pold]+" - "+mlist[3][pold]);
	}
	
	void putLastSong(){
		zdb.putInteger("lastSong",pold);
		zdb.refresh();
	}

	@Override
	protected void onDestroy(){
		if(noti != null)
			mNotificationManager.cancel(0);
		super.onDestroy();
	}

	@Override
	public void onLowMemory(){
		if(noti != null)
			mNotificationManager.cancel(0);
		super.onLowMemory();
	}
	
	long getTimerMode(){
		long time = 0;
		switch(zdb.getInteger("sleep",0)){
			case 1:
				time = 5;
				break;
			case 2:
				time = 10;
				break;
			case 3:
				time = 15;
				break;
			case 4:
				time = 30;
				break;
			case 5:
				time = 45;
				break;
			case 6:
				time = 60;
				break;
			case 7:
				time = 75;
				break;
			case 8:
				time = 90;
				break;
			case 9:
				time = 120;
				break;
			case 0:
			default:
				return 999;
		} return time*60000;
	}
	
	int repeat = 0;
	long target = 0;
	
	void sleepTimer(){
		if(getTimerMode() != 999){
			if(repeat == 0){
				target = System.currentTimeMillis()+getTimerMode();
				repeat++;
			} new Handler().postDelayed(new Runnable(){
				public void run(){
					if((System.currentTimeMillis() >= target)){
						if(mp != null && mp.isPlaying()){
							play(false);
							zdb.putInteger("sleep",0);
							zdb.refresh();
							fillSettings();
							repeat = 0;
							setNoti(pold,true);
						}
					} else sleepTimer();
				}
			},250);
		}
	}
	
	private class StatusBarRecv extends BroadcastReceiver{
		@Override
		public void onReceive(Context p1, Intent p2){
			switch(p2.getAction()){
				case "org.superdroid.music.PREV":
					if(rep != 2 && rep != 3) prev();
					break;
				case "org.superdroid.music.PAUSE":
					play(false);
					break;
				case "org.superdroid.music.NEXT":
					if(rep != 2) next();
					break;
				case "org.superdroid.music.PLAY":
					play(true);
					break;
				default:
					Toast.makeText(getBaseContext(),"Unknown",Toast.LENGTH_SHORT).show();
					break;
			} abortBroadcast();
		}
	}
	
	private class BecomingNoisyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction()))
				play(false);
		}
	}
	
	private void scan(){
		String mediaService = "com.android.providers.media";
		Bundle b = new Bundle();
		b.putString("volume","external");
		startService((new Intent()).setComponent(new ComponentName(mediaService, mediaService+".MediaScannerService")).putExtras(b));
		showToast(getResources().getString(R.string.medyatara2));
	}
	
	void showToast(String text){
		tv.setText(text);
		t.show();
	}

	LinearLayout toast(){
		LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.MATCH_PARENT,
			LinearLayout.LayoutParams.MATCH_PARENT);
		LinearLayout ll = new LinearLayout(this);
		ll.setGravity(Gravity.CENTER_VERTICAL);
		ll.setLayoutParams(llp);
		ll.setBackgroundColor(Color.parseColor("#66303030"));
		ll.setPadding(48,48,48,48);
		ll.setGravity(Gravity.CENTER_HORIZONTAL);
		tv = new TextView(this);
		tv.setLayoutParams(llp);
		tv.setTextColor(getResources().getColor(android.R.color.white));
		tv.setTextSize(20);
		tv.setGravity(Gravity.CENTER_HORIZONTAL);
		tv.setTypeface(Typeface.create("sans-serif-light",Typeface.NORMAL));
		ll.addView(tv);
		return ll;
	}
	
	Bitmap getAlbumArt(){
		synchronized(this){
			try {
				Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
				Uri artUri = ContentUris.withAppendedId(sArtworkUri, Long.parseLong(mlist[5][pold]));
				return MediaStore.Images.Media.getBitmap(this.getContentResolver(), artUri);
			} catch(Exception e){
				return null;
			}
		}
	}
	
	public int getStatusBarHeight(){ 
		int result = 0;
		int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0)
			result = getResources().getDimensionPixelSize(resourceId);
		return result;
	} 
	
	public int getNavigationBarHeight(){
		int result = 0;
		int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
		if (resourceId > 0)
			result = getResources().getDimensionPixelSize(resourceId);
		return result;
	}
	
	
	public Bitmap fastblur(Bitmap sentBitmap){
		try{
			float scale = 0.65f;
			int radius = 10;
			int width = Math.round(sentBitmap.getWidth() * scale);
			int height = Math.round(sentBitmap.getHeight() * scale);
			sentBitmap = Bitmap.createScaledBitmap(sentBitmap, width, height, false);
			Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
			if (radius < 1) return null;
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			int[] pix = new int[w * h];
			bitmap.getPixels(pix, 0, w, 0, 0, w, h); 
			int wm = w - 1; 
			int hm = h - 1; 
			int wh = w * h; 
			int div = radius + radius + 1; 
			int r[] = new int[wh]; 
			int g[] = new int[wh]; 
			int b[] = new int[wh]; 
			int rsum, gsum, bsum, x, y, i, p, yp, yi, yw; 
			int vmin[] = new int[Math.max(w, h)]; 
			int divsum = (div + 1) >> 1;
			divsum *= divsum;
			int dv[] = new int[256 * divsum];
			for (i = 0; i < 256 * divsum; i++)
				dv[i] = (i / divsum);
			yw = yi = 0;
			int[][] stack = new int[div][3];
			int stackpointer;
			int stackstart;
			int[] sir;
			int rbs;
			int r1 = radius + 1;
			int routsum, goutsum, boutsum;
			int rinsum, ginsum, binsum;
			for (y = 0; y < h; y++){
				rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
				for (i = -radius; i <= radius; i++){
					p = pix[yi + Math.min(wm, Math.max(i, 0))];
					sir = stack[i + radius];
					sir[0] = (p & 0xff0000) >> 16;
					sir[1] = (p & 0x00ff00) >> 8;
					sir[2] = (p & 0x0000ff);
					rbs = r1 - Math.abs(i);
					rsum += sir[0] * rbs;
					gsum += sir[1] * rbs;
					bsum += sir[2] * rbs;
					if (i > 0){
						rinsum += sir[0];
						ginsum += sir[1];
						binsum += sir[2];
					} else {
						routsum += sir[0];
						goutsum += sir[1];
						boutsum += sir[2];
					}
				} stackpointer = radius;

				for (x = 0; x < w; x++){
					r[yi] = dv[rsum]; 
					g[yi] = dv[gsum];
					b[yi] = dv[bsum];
					rsum -= routsum;
					gsum -= goutsum;
					bsum -= boutsum;
					stackstart = stackpointer - radius + div;
					sir = stack[stackstart % div];
					routsum -= sir[0];
					goutsum -= sir[1];
					boutsum -= sir[2];
					if (y == 0) vmin[x] = Math.min(x + radius + 1, wm);
					p = pix[yw + vmin[x]];
					sir[0] = (p & 0xff0000) >> 16;
					sir[1] = (p & 0x00ff00) >> 8;
					sir[2] = (p & 0x0000ff);
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
					rsum += rinsum;
					gsum += ginsum;
					bsum += binsum;
					stackpointer = (stackpointer + 1) % div;
					sir = stack[(stackpointer) % div];
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
					rinsum -= sir[0];
					ginsum -= sir[1];
					binsum -= sir[2];
					yi++;
				} yw += w;
			}
			
			for (x = 0; x < w; x++) {
				rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
				yp = -radius * w;
				for (i = -radius; i <= radius; i++){
					yi = Math.max(0, yp) + x;
					sir = stack[i + radius];
					sir[0] = r[yi];
					sir[1] = g[yi];
					sir[2] = b[yi];
					rbs = r1 - Math.abs(i);
					rsum += r[yi] * rbs;
					gsum += g[yi] * rbs;
					bsum += b[yi] * rbs;
					if (i > 0){
						rinsum += sir[0];
						ginsum += sir[1];
						binsum += sir[2];
					} else {
						routsum += sir[0];
						goutsum += sir[1];
						boutsum += sir[2];
					}

					if (i < hm) yp += w;
				} yi = x;
				stackpointer = radius;
				
				for (y = 0; y < h; y++){
					pix[yi] = ( 0xff000000 & pix[yi] ) | ( dv[rsum] << 16 ) | ( dv[gsum] << 8 ) | dv[bsum];
					rsum -= routsum;
					gsum -= goutsum;
					bsum -= boutsum;
					stackstart = stackpointer - radius + div;
					sir = stack[stackstart % div];
					routsum -= sir[0];
					goutsum -= sir[1];
					boutsum -= sir[2];
					if (x == 0)
						vmin[y] = Math.min(y + r1, hm) * w;
					p = x + vmin[y];
					sir[0] = r[p];
					sir[1] = g[p];
					sir[2] = b[p];
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
					rsum += rinsum;
					gsum += ginsum;
					bsum += binsum;
					stackpointer = (stackpointer + 1) % div;
					sir = stack[stackpointer];
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
					rinsum -= sir[0];
					ginsum -= sir[1];
					binsum -= sir[2];
					yi += w;
				}
			} bitmap.setPixels(pix, 0, w, 0, 0, w, h);
			return bitmap;
		} catch(Exception e){ return sentBitmap; }
	}
	
	String getStr(int startValue, int skipValue){
		return getResources().getString(startValue+skipValue);
	}
	
}
