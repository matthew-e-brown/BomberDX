boolean up, down, left, right, spacebar = false;

void keyPressed() {
  if (key == 'w' || key == 'W') up = true;
  if (key == 's' || key == 'S') down = true;
  if (key == 'a' || key == 'A') left = true;
  if (key == 'd' || key == 'D') right = true;
  if (keyCode == 32) spacebar = true;
  if (mainGame && (keyCode == 10 || keyCode == 13)) paused = !paused;
}

void keyReleased() {
  if (key == 'w' || key == 'W') up = false;
  if (key == 's' || key == 'S') down = false;
  if (key == 'a' || key == 'A') left = false;
  if (key == 'd' || key == 'D') right = false;
  if (keyCode == 32) spacebar = false;
}

void mousePressed() {
  if (mainMenu) {
    if (mB[playButton].mouseOver()) {
      mainMenu = false;
      loadScreen = true;
      optionsMenu = false;
      defeatScreen = false;
      victoryScreen = false;
      credScreen = false;
      mainGame = false;
    } else if (mB[opButton].mouseOver()) {
      optionsMenuSetup();
      mainMenu = false;
      optionsMenu = true;
      defeatScreen = false;
      victoryScreen = false;
      credScreen = false;
      mainGame = false;
    } else if (mB[credButton].mouseOver()) {
      optionsMenuSetup();
      mainMenu = false;
      optionsMenu = false;
      defeatScreen = false;
      victoryScreen = false;
      credScreen = true;
      mainGame = false;
    } else if (mB[quitButton].mouseOver()) {
      exit();
    }
  } else if (optionsMenu) {
    for (floatSlider s : oS) {
      if (s.mouseOver()) s.held = true;
    }
    if (planeToggle.mouseOver()) planeToggle.var = !planeToggle.var;
    if (oB[backButton].mouseOver()) {
      allButMainMenu();
    }
  } else if (credScreen) {
    if (oB[backButton].mouseOver()) {
      allButMainMenu();
    }
  } else if (victoryScreen || defeatScreen) {
    if (eB[eBrestart].mouseOver()) {
      amtLoaded = 0;
      setLoadBoolsFalse();
      setCompBoolsFalse();
      backtoLoading();
    } else if (eB[eBmenu].mouseOver()) {
      amtLoaded = 0;
      setLoadBoolsFalse();
      setCompBoolsFalse();
      allButMainMenu();
    } else if (eB[eBquit].mouseOver()) {
      exit();
    }
  } else if (mainGame && paused) {
    if (pB[resume].mouseOver()) {
      paused = false;
    } else if (pB[home].mouseOver()) {
      amtLoaded = 0;
      setLoadBoolsFalse();
      setCompBoolsFalse();
      allButMainMenu();
    } else if (pB[restart].mouseOver()) {
      amtLoaded = 0;
      setLoadBoolsFalse();
      setCompBoolsFalse();
      backtoLoading();
    } else if (pB[quit].mouseOver()) {
      exit();
    }
  }
}


void mouseReleased() {
  if (mainMenu) {
  } else if (optionsMenu) {
    for (floatSlider s : oS) {
      s.held = false;
    }
  }
}

void setLoadBoolsFalse() {
  gameLoadStarted = false;
  gridLoadStarted = false;
  explosionLoadStarted = false;
  bombLoadStarted = false;
  planeLoadStarted = false;
  deadPlaneLoadStarted = false;
}

void setCompBoolsFalse() {
  gameLoadCompleted = false;
  gridLoadCompleted = false; 
  explosionLoadCompleted = false; 
  bombLoadCompleted = false; 
  planeLoadCompleted = false; 
  deadPlaneLoadCompleted = false;
}

void allButMainMenu() {
  mainGame = false;
  paused = false;
  mainMenu = true;
  loadScreen = false;
  optionsMenu = false;
  defeatScreen = false;
  victoryScreen = false;
  credScreen = false;
}

void backtoLoading() {
  mainGame = false;
  paused = false;
  mainMenu = false;
  loadScreen = true;
  optionsMenu = false;
  defeatScreen = false;
  victoryScreen = false;
  credScreen = false;
}
