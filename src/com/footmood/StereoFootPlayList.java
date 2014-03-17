package com.footmood;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class StereoFootPlayList {
	private Context context;
	
	public StereoFootPlayList(Context context){
		this.context = context;
	}
	
	public Cursor getandroidPlaylistcursor(Mood mood)
	{	
	    ContentResolver resolver = context.getContentResolver();
	    final Uri uri=MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
	    final String id = MediaStore.Audio.Playlists._ID;
	    final String name = MediaStore.Audio.Playlists.NAME;
	    final String[]columns = {id,name};
	    final String criteria = MediaStore.Audio.Playlists.NAME + " = ?" ;
	    final Cursor c = resolver.query(uri, columns, criteria, new String[]{mood.name().toLowerCase()},null);
	    return c;
	}

	public  Cursor getPlaylistSongsCursor(Integer playListId)
	{	
	    ContentResolver resolver = context.getContentResolver();
	    final Uri uri=MediaStore.Audio.Playlists.Members.getContentUri("external", playListId);
	    final String id = MediaStore.Audio.Playlists.Members._ID;
	    final String title = MediaStore.Audio.Playlists.Members.TITLE;
	    final String data = MediaStore.Audio.Playlists.Members.DATA;
	    
	    final String[]columns = {id,title, data};
	    final Cursor c = resolver.query(uri, columns, null, null,MediaStore.Audio.Playlists.Members.PLAY_ORDER);
	    return c;
	}
	
	public Cursor getSongsList(Mood mood){
	    Cursor c = null;
		//recupero la playlistid
		Cursor cplay = getandroidPlaylistcursor(mood);
		if(cplay.moveToNext()){
			Integer playListid = cplay.getInt(cplay.getColumnIndex(MediaStore.Audio.Playlists._ID));
			//recupero le canzoni
			c = getPlaylistSongsCursor(playListid);
		}
		return c;
	}

}
