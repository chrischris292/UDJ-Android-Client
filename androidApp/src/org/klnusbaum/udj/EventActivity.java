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
package org.klnusbaum.udj;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.TabHost;
import android.view.View;
import android.content.Context;
import android.accounts.AccountManager;
import android.accounts.Account;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.util.Log;
import android.app.Dialog;
import android.app.SearchManager;
import android.net.Uri;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.DialogFragment;

import java.util.HashMap;

import org.klnusbaum.udj.auth.AuthActivity;
import org.klnusbaum.udj.actionbar.ActionBarActivity;
import org.klnusbaum.udj.Constants;
import org.klnusbaum.udj.network.PlaylistSyncService;
import org.klnusbaum.udj.network.EventCommService;

/**
 * The main activity display class.
 */
public class EventActivity extends ActionBarActivity{

  private static final String QUIT_DIALOG_TAG = "quit_dialog";

  private Account account;
  private long eventId;

  @Override
  protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    eventId = getIntent().getLongExtra(Constants.EVENT_ID_EXTRA, -1);
    account = (Account)getIntent().getParcelableExtra(Constants.ACCOUNT_EXTRA);
    //TODO handle if no event id or account was given 
    
    FragmentManager fm = getSupportFragmentManager();
    if(fm.findFragmentById(android.R.id.content) == null){
      PlaylistFragment list = new PlaylistFragment();
      fm.beginTransaction().add(android.R.id.content, list).commit();
    }
    Intent getPlaylist = new Intent(
      Intent.ACTION_VIEW,
      UDJEventProvider.PLAYLIST_URI,
      this,
      PlaylistSyncService.class);
    getPlaylist.putExtra(Constants.EVENT_ID_EXTRA, eventId);
    getPlaylist.putExtra(Constants.ACCOUNT_EXTRA, account);
    startService(getPlaylist);
  }

  @Override
  protected void onNewIntent(Intent intent){
    if(Intent.ACTION_SEARCH.equals(intent.getAction())){
      intent.setClass(this, AvailableMusicSearchActivity.class);
      intent.putExtra(Constants.ACCOUNT_EXTRA, account);
      intent.putExtra(Constants.EVENT_ID_EXTRA, eventId);
      startActivity(intent);
    }
  }

  @Override 
  public void onBackPressed(){
    DialogFragment newFrag = new QuitDialogFragment();
    newFrag.show(getSupportFragmentManager(), QUIT_DIALOG_TAG);
  }

  private void doQuit(){
    dismissQuitDialog();
    Intent leaveEvent = new Intent(
      Intent.ACTION_DELETE,
      new Uri.Builder().authority(Constants.AUTHORITY).appendPath("event").build(),
      this,
      EventCommService.class);
    leaveEvent.putExtra(Constants.EVENT_ID_EXTRA, eventId);
    leaveEvent.putExtra(Constants.ACCOUNT_EXTRA, account);
    startService(leaveEvent);
    setResult(Activity.RESULT_OK);
    finish();
  }

  private void dismissQuitDialog(){
    QuitDialogFragment qd = 
      (QuitDialogFragment)getSupportFragmentManager().findFragmentByTag(QUIT_DIALOG_TAG);
    qd.dismiss();
  }

  public static class QuitDialogFragment extends DialogFragment{
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
      return new AlertDialog.Builder(getActivity())
        .setTitle(R.string.quit_title)
        .setMessage(R.string.quit_message)
        .setPositiveButton(android.R.string.ok,
          new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton){
              ((EventActivity)getActivity()).doQuit();
            }
          })
        .setNegativeButton(android.R.string.cancel,
          new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton){
              ((EventActivity)getActivity()).dismissQuitDialog();
            }
          })
        .create();
    }
  }
}

