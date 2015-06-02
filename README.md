# LayoutCoordinatorExample
Working on an example which will hopefully be turned into a library for ViewPager headers.

![Alt text](https://github.com/hunterrobbert/LayoutCoordinatorExample/blob/master/scrot.png "screenshot")

## Plans
To create a variety of header layouts whose views, motion and scroll behaviors are customizable and extensible.  The
current approach is to have a LayoutCoordinator which registers scrollable views with a header layouts views.  The LayoutCoordinator
would be responsible for watching and updateing the scroll position of all registered views and translating header views 
according to specified behaviors.  

This project is barely started.  Here are some things that need to be done:
- Abstract away scroll calculations from LayoutCoordinator, making LayoutCoordinator agnostic to the type of scrollable views it has
regisered
- Add an easy way to add views to an included header layout (and define how they are to be coordinated/translated on scroll)
- Add predefined behaviors for header layout views.
- Remove the need for a custom RecyclerView adapter (currently it takes care of making a header to push the view down below the 
header layout and a footer to allow the view to be scrolled all the way to the top of the screen)
- Finish implementing custom attributes for CollapsibleHeaderLayout - such as the 'autoHideHeader' functionality 
- Lots more that I can't think of at the moment
- Actually pull everything out and into a library
