/**
 * Copyright 2011 Kurtis L. Nusbaum
 * 
 * This file is part of UDJ.
 * 
 * UDJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * 
 * UDJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with UDJ.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.klnusbaum.udj.network;

import java.util.List;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
//import android.content.ContentResolver;
//import android.content.ContentProviderOperation;
//import android.content.OperationApplicationException;
import android.util.Log;
//import android.os.RemoteException;
import android.accounts.Account;
import android.accounts.AccountManager;

import org.klnusbaum.udj.Constants;
//import org.klnusbaum.udj.UDJPlayerProvider;
import org.klnusbaum.udj.containers.ActivePlaylistEntry;
import org.klnusbaum.udj.Utils;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;


public class RESTProcessor{

  public static final String TAG = "RESTProcessor";

  private static void checkVolume(Context context, AccountManager am, Account account, int volume){
    if(Utils.getPlayerVolume(am, account) != volume){
      am.setUserData(account, Constants.PLAYER_VOLUME_DATA, String.valueOf(volume));
      Intent playerVolumeChangedBroadcast = new Intent(Constants.BROADCAST_VOLUME_CHANGED);
      playerVolumeChangedBroadcast.putExtra(Constants.PLAYER_VOLUME_EXTRA, volume);
      context.sendBroadcast(playerVolumeChangedBroadcast);
    }
  }

  private static void checkPlaybackState(Context context, AccountManager am, Account account, String playbackState){
    int plState = Constants.PLAYING_STATE;
    if(playbackState.equals("playing")){
      plState = Constants.PLAYING_STATE;
    }
    else if(playbackState.equals("paused")){
      plState = Constants.PAUSED_STATE;
    }
    if(Utils.getPlaybackState(am, account) != plState){
      am.setUserData(account, Constants.PLAYBACK_STATE_DATA, String.valueOf(plState));
      Intent playbackStateChangedBroadcast = new Intent(Constants.BROADCAST_PLAYBACK_CHANGED);
      playbackStateChangedBroadcast.putExtra(Constants.PLAYBACK_STATE_EXTRA, plState);
      context.sendBroadcast(playbackStateChangedBroadcast);
    }
  }

  public static List<ActivePlaylistEntry> processActivePlaylist(
    JSONObject activePlaylist,
    AccountManager am,
    Account account,
    Context context)
    throws JSONException
  {
    checkPlaybackState(context, am, account, activePlaylist.getString("state"));
    checkVolume(context, am, account, activePlaylist.getInt("volume"));
    ActivePlaylistEntry currentSong = 
      ActivePlaylistEntry.valueOf(activePlaylist.getJSONObject("current_song"));
    currentSong.setCurrentSong(true);
    List<ActivePlaylistEntry> playlist = ActivePlaylistEntry.fromJSONArray(
      activePlaylist.getJSONArray("active_playlist"));
    playlist.add(0, currentSong);
    return playlist;
  }

}
