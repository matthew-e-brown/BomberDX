class Tile {
  PVector pos, displaypos;
  PImage sprite;

  float health, maxHealth;
  int spin, type;
  boolean base, dead;

  Tile(float x, float y, float z, int type) {
    this.type = type;
    pos = new PVector(x, y, z);
    displaypos = new PVector(x, y, z);
    sprite = loadImage("Assets/Sprites/Tiles/" + type + ".png");
    //rotate randomly
    spin = floor(random(1, 5));
    base = false;
    if (type >= 11 && type <= 14) {
      health = maxHealth = 100;
      base = true;
      dead = false;
    }
  }

  void display() {
    pushMatrix();
    {
      translate(displaypos.x, displaypos.y, displaypos.z);
      rotate(PI/2 * spin);
      imageMode(CENTER);
      image(sprite, 0, 0);
    }
    popMatrix();
  }

  boolean isOver(PVector objpos) {
    boolean wid = false;
    boolean hei = false;
    if (objpos.x > pos.x-sprite.width/2 && objpos.x < pos.x+sprite.width/2) wid = true;
    if (objpos.y > pos.y-sprite.height/2 && objpos.y < pos.y+sprite.height/2) hei = true;
    if (wid && hei) return true;
    else return false;
  }

  void healthBar() {
    float w = sprite.width*0.80;
    float h = sprite.height/6;
    pushMatrix();
    {
      translate(displaypos.x, displaypos.y+sprite.height*0.65, displaypos.z);
      rectMode(CENTER);
      fill(50);
      //stroke(0);
      //strokeWeight(2);
      noStroke();
      rect(0, 0, w, h, h/2);
      fill(#16A7DE);
      noStroke();
      rectMode(CORNER);
      rect(-w/2 + h/8, -h/2 + h/8, map(constrain(health, 0, maxHealth), maxHealth, 0, w - h/4, 0), h - h/4, h/2);
    }
    popMatrix();
  }
}
