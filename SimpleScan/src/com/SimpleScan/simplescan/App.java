package com.SimpleScan.simplescan;

import com.parse.Parse;

import android.app.Application;

public class App extends Application {
  @Override
  public void onCreate() {
    super.onCreate();

    Parse.enableLocalDatastore(this);
    Parse.initialize(this, "a4ZWiuSvTkStw4mhxT909tFBx5hFxvckFVGnP6Kz", "FZviixdeF8nyCwZdIsqbIvd7seOnqDFQfOFIng6X");
    
  }
}