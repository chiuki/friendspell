package com.sqisland.friendspell.activity;

import android.view.Menu;
import android.view.MenuItem;

import com.sqisland.friendspell.storage.NearbyPerson;

public class NearbyActivity extends BaseNearbyActivity {
  private static final NearbyPerson[] PEOPLE = new NearbyPerson[] {
      new NearbyPerson("107708120842840792570", "A", "Adam Powell", "https://lh3.googleusercontent.com/-u6tKuQpTVNA/AAAAAAAAAAI/AAAAAAAAHO4/irJdIXh_XIs/s46-c-k-no/photo.jpg"),
      new NearbyPerson("111805907936971909430", "A", "Alexander Lucas", "https://lh3.googleusercontent.com/-lknLWVT-9_w/AAAAAAAAAAI/AAAAAAAAKtY/O7IgG7pyOZM/s46-c-k-no/photo.jpg"),
      new NearbyPerson("117509657298845443204", "B", "Benjamin Weiss", "https://lh3.googleusercontent.com/-b8Oxh-bDUMI/AAAAAAAAAAI/AAAAAAAA8s0/iobDgNYYC9g/s46-c-k-no/photo.jpg"),
      new NearbyPerson("104755487586666698979", "C", "Chet Haase", "https://lh3.googleusercontent.com/-alRF2kfXilM/AAAAAAAAAAI/AAAAAAAAz5I/dZZpm1WTwE4/s46-c-k-no/photo.jpg"),
      new NearbyPerson("105062545746290691206", "C", "Colt McAnlis", "https://lh3.googleusercontent.com/-NHsYU-OGvnQ/AAAAAAAAAAI/AAAAAAAAH2E/5FBt4OXZH80/s46-c-k-no/photo.jpg"),
      new NearbyPerson("114592751246503219483", "D", "Dan Sandler", "https://lh3.googleusercontent.com/-rUpfE68lDyQ/AAAAAAAAAAI/AAAAAAAEVRI/J6h6j-HSDNI/s46-c-k-no/photo.jpg"),
      new NearbyPerson("105051985738280261832", "D", "Dianne Hackborn", "https://lh3.googleusercontent.com/-cyZL14lm8gk/AAAAAAAAAAI/AAAAAAAAIFY/4HCpUx9y2W4/s46-c-k-no/photo.jpg"),
      new NearbyPerson("109486821799932251955", "I", "Ian Ni-Lewis", "https://lh3.googleusercontent.com/-llT3VlUruS0/AAAAAAAAAAI/AAAAAAAAQo0/NyeObH6Ds48/s46-c-k-no/photo.jpg"),
      new NearbyPerson("104523162451451372243", "J", "Joanna Smith", "https://lh3.googleusercontent.com/-7VfLXXUd2D4/AAAAAAAAAAI/AAAAAAAAHbM/Qz6AG3173Hc/s46-c-k-no/photo.jpg"),
      new NearbyPerson("101483589011642884895", "K", "Katherine Kuan", "https://lh3.googleusercontent.com/-fF-088TJRMQ/AAAAAAAAAAI/AAAAAAAAABE/Qh-ekQUjrmg/s46-c-k-no/photo.jpg"),
      new NearbyPerson("108761828584265913206", "K", "Kirill Grouchnikov", "https://lh3.googleusercontent.com/-ZAZcdI59VRY/AAAAAAAAAAI/AAAAAAABOUA/kw8jV5gEiWg/s46-c-k-no/photo.jpg"),
      new NearbyPerson("118292708268361843293", "N", "Nick Butcher", "https://lh3.googleusercontent.com/-O2_Tp9UfVjY/AAAAAAAAAAI/AAAAAAAAc0w/VuHK2RvzybY/s46-c-k-no/photo.jpg"),
      new NearbyPerson("111169963967137030210", "R", "Reto Meier", "https://lh3.googleusercontent.com/-FnQer4-BRHE/AAAAAAAAAAI/AAAAAAAA-hU/AJ4qvN5ammg/s46-c-k-no/photo.jpg"),
      new NearbyPerson("113735310430199015092", "R", "Roman Nurik", "https://lh3.googleusercontent.com/--cY9yaYlePg/AAAAAAAAAAI/AAAAAAAASNo/VnQGcdz_luI/s46-c-k-no/photo.jpg"),
      new NearbyPerson("113751353481962008916", "T", "Timothy Jordan", "https://lh3.googleusercontent.com/-LRRTHQQ-NRY/AAAAAAAAAAI/AAAAAAABkdk/pAF-pXO7hbY/s46-c-k-no/photo.jpg"),
      new NearbyPerson("116539451797396019960", "T", "Tor Norbye", "https://lh3.googleusercontent.com/-LNdnF_dvn90/AAAAAAAAAAI/AAAAAAABcf0/CYW5PSDoTcM/s46-c-k-no/photo.jpg"),
      new NearbyPerson("109385828142935151413", "X", "Xavier Ducrohet", "https://lh3.googleusercontent.com/-TB5RgVFwuWE/AAAAAAAAAAI/AAAAAAAAD0o/NtNoQP9QdPw/s46-c-k-no/photo.jpg")
  };

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    for (NearbyPerson person : PEOPLE) {
      menu.add(person.displayName);
    }
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    CharSequence title = item.getTitle();
    if (title != null) {
      for (NearbyPerson person : PEOPLE) {
        if (title.toString().equals(person.displayName)) {
          addPerson(person);
          updateList();
          return true;
        }
      }
    }
    return super.onOptionsItemSelected(item);
  }
}