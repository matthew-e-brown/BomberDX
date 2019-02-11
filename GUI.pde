class Button {
  float x, y, w, h, tSize;
  PImage sprite;
  String text;
  color fill, stroke, hoverFill, hoverStroke, nFill, nStroke;
  boolean image;
  boolean centre, textInvert = false;
  Button(float x, float y, float w, float h, String text, float size) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.text = text;
    this.tSize = size;
    hoverFill = color(160);
    hoverStroke = color(80, 185, 255);
    nFill = color(245);
    nStroke = color(75);
    fill = nFill;
    stroke = nStroke;
    image = false;
  }


  Button(float x, float y, float w, float h, String text, float size, String sprite) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.text = text;
    this.tSize = size;
    fill = color(245);
    stroke = color(75);
    this.sprite = loadImage("Assets/Sprites/buttons/" + sprite + ".png");
    image = true;
  }

  boolean mouseOver() {
    boolean ex = false;
    boolean why = false;
    if (mouseX >= x - w/2 && mouseX <= x + w/2) ex = true;
    if (mouseY >= y - h/2 && mouseY <= y + h/2) why = true;
    if (ex && why) return true;
    else return false;
  }

  void update() {
    if (!image) {
      if (!mouseOver()) {
        fill = nFill;
        stroke = nStroke;
      } else if (mouseOver()) {
        fill = hoverFill;
        stroke = hoverStroke;
      }
    }
  }

  void display() {
    pushMatrix();
    {
      translate(x, y);
      rectMode(CENTER);
      fill(fill);
      if (!mouseOver()) {
        noStroke();
        //stroke(stroke);
        //strokeWeight(4);
        rect(0, 0, w, h, 25);
      }
      imageMode(CENTER);
      if (image && mouseOver()) image(sprite, 0, 0);
      else if (!image) rect(0, 0, w, h, 25);
      fill(0);
      if (textInvert && mouseOver()) fill(255);
      if (centre) textAlign(CENTER, CENTER);
      else textAlign(LEFT, CENTER);
      textFont(bold);
      textSize(tSize);
      if (!centre) text(text, -w/2 + 35, 0);
      else text(text, 0, 0);
    }
    popMatrix();
  }
}

class floatSlider {
  float x, y, w, h;
  float min, max;
  float var;
  float sliderpos;
  boolean held = false;
  String title;
  PImage gradient;
  boolean grad = false;
  floatSlider(float x, float y, float w, float h) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
  }

  floatSlider(float x, float y, float w, float h, String title) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.title = title;
  }

  floatSlider(float x, float y, float w, float h, String title, String gradient) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.title = title;
    this.gradient = loadImage("Assets/Sprites/sliders/" + gradient + ".png");
    this.gradient.resize(round(this.w), this.gradient.height);
    grad = true;
  }

  void setVar(float var, float min, float max) {
    this.var = var;
    this.min = min;
    this.max = max;
    this.sliderpos = map(var, min, max, x-w/2, x+w/2);
  }

  float getVar() {
    return var;
  }

  boolean mouseOver() {
    if (dist(mouseX, mouseY, sliderpos, y) <= h) return true;
    else return false;
  }

  void update() {
    if (held) {
      sliderpos = constrain(mouseX, x-w/2, x+w/2);
      var = map(sliderpos, x-w/2, x+w/2, min, max);
    }
  }

  void display() {
    pushMatrix();
    {
      rectMode(CENTER);
      translate(x, y);
      fill(255);
      stroke(0);
      strokeWeight(2);
      if (!grad) rect(0, 0, w, h/2, h/4);
      else {
        image(gradient, 0, 0);
        rectMode(CORNERS);
        fill(0);
        noStroke();
        rect(sliderpos-x, -gradient.height/2, w/2, gradient.height/2, h/4);
      }
      pushMatrix();
      {
        translate(sliderpos-x, 0, 2);
        fill(255);
        stroke(0);
        ellipse(0, 0, h, h);
      }
      popMatrix();
    }
    popMatrix();

    if (mouseOver() || held) {
      pushMatrix(); 
      {
        rectMode(CENTER);
        translate(sliderpos, y + h*1.85, 1);
        fill(255);
        stroke(0);
        int roundvar = round(var);
        if (roundvar - 100 < 0) rect(0, 0, h*1.5, h*1.5);
        else if (roundvar - 100 >= 0) rect(0, 0, h*1.85, h*1.5);
        fill(0);
        textAlign(CENTER, CENTER);
        textFont(bold);
        textSize(24);
        text(roundvar, 0, 0);
      } 
      popMatrix();
    }
  }
}

class Switch { //inb4 DMCA from Nintendo
  float x, y, w, h, r;
  boolean var;
  int textSize;
  String[] LRtext = new String[2];
  String title;
  Switch(float x, float y, float w, float h, String title, int textSize) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.r = h*7/8;
    this.title = title;
    this.textSize = textSize;
  }

  void setVar(boolean var) { 
    this.var = var;
  }

  void setVar(boolean var, String left, String right) {
    this.var = var;
    this.LRtext[0] = left;
    this.LRtext[1] = right;
  }

  void setVar(int var) {
    if (var == 0) this.var = false;
    else if (var == 1) this.var = true;
    else println("ERROR! Slider expects int of 0 or 1");
  }

  void setVar(int var, String left, String right) {
    if (var == 0) this.var = false;
    else if (var == 1) this.var = true;
    else println("ERROR! Slider expects int of 0 or 1");
    this.LRtext[0] = left;
    this.LRtext[1] = right;
  }

  boolean getVar() {
    return var;
  }

  int getVarAsInt() {
    if (!var) return 0;
    else return 1;
  }

  boolean mouseOver() {
    boolean wid = false;
    boolean hei = false;
    if (mouseX >= x - w/2 && mouseX <= x + w/2) wid = true;
    if (mouseY >= y - h/2 && mouseY <= y + h/2) hei = true;
    if (wid && hei) return true;
    else return false;
  }

  void display() {
    pushMatrix(); 
    {
      translate(x, y);
      rectMode(CENTER);
      strokeWeight(2);
      stroke(0);
      fill(255);
      rect(0, 0, w, h, r);
      fill(210);
      rectMode(CORNERS);
      float left = 0 - w/2 + w*0.055;
      float right = 0 + w/2 - w*0.055;
      float top = 0 - h/2 + w*0.055;
      float bottom = 0 + h/2 - w*0.055;
      if (var == false) rect(left, top, 0, bottom, r);
      else if (var == true) rect(0, bottom, right, top, r);
      for (int i = 0; i < LRtext.length; i++) {
        if (LRtext[i] != "") { //False = left, True = right //left = 0; right = 1;
          float boxWidth = LRtext[i].length() * 20;
          rectMode(CENTER);
          fill(255);
          stroke(0);
          strokeWeight(2);
          rect(0 + w/2 + boxWidth*0.65 - i*2*(w/2+boxWidth*0.65), 0, boxWidth, h*1.12);
          fill(0);
          textAlign(CENTER, CENTER);
          textFont(bold);
          textSize(textSize);
          text(LRtext[1-i], 0 + w/2 + boxWidth*0.65 - i*2*(w/2+boxWidth*0.65), 0);
        }
      }
    } 
    popMatrix();
  }
}
