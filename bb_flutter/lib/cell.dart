import 'dart:collection';
import 'dart:convert';

import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:carousel_slider/carousel_slider.dart';
import 'dart:developer' as dev;
import 'dart:math';

// This is on alternate entrypoint for this module to display Flutter UI in
// a (multi-)view integration scenario.
void main() {
  runApp(const Cell());
}

class Cell extends StatefulWidget {
  const Cell({Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() => _CellState();
}

class _CellState extends State<Cell> with WidgetsBindingObserver {
  static const PAGE_SCROLL_ANIMATION_DURATION = 800;
  static const SCROLL_DURATION = 3;
  static const String BANNER_METHOD_CHANNEL = "dynamicPageBanner";
  static const channel = MethodChannel(BANNER_METHOD_CHANNEL);

  List<Map<String, dynamic>> sectionData = List.empty(growable: true);
  AppLifecycleState? appLifecycleState;

  int _current = 0;
  List<Widget> imageSliders = List.empty();
  List<CachedNetworkImageProvider> imageProviders = List.empty(growable: true);

  final CarouselController _controller = CarouselController();
  final PageController _pageController = PageController(viewportFraction: 0.99);
  int _currentPage = 0;
  bool loading = true;

  @override
  void initState() {
    const channel = MethodChannel('my_cell');
    channel.setMethodCallHandler((call) async {
      if (call.method == 'setCellData') {
        setState(() {
          //cellNumber = call.arguments as int;
          if (sectionData.isNotEmpty) {
            sectionData.clear();
          }
          List<Object?> sectionDataRaw = call.arguments as List<Object?>;
          for (var element in sectionDataRaw) {
            if (element != null) {
              sectionData.add(HashMap<String, dynamic>.from(
                  json.decode(jsonEncode(element))));
            }
          }
          //_random = Random(cellNumber);

          if (imageSliders.isNotEmpty) {
            imageSliders.clear();
          }

          if (imageProviders.isNotEmpty) {
            imageProviders.clear();
          }

          imageSliders = sectionData
              .map((item) => ClipRRect(
                    child: InkWell(
                        onTap: () {
                          dev.log("Clicked item: $item");
                          channel.invokeMethod("onBannerClick", item);
                        },
                        child: CachedNetworkImage(
                          imageUrl: item["image"] as String,
                          imageBuilder: (context, imageProvider) => Container(
                            decoration: BoxDecoration(
                              image: DecorationImage(
                                image: imageProvider,
                                fit: BoxFit.fill,
                              ),
                            ),
                          ),
                          placeholder: (context, url) =>
                              const CircularProgressIndicator(),
                          errorWidget: (context, url, error) =>
                              const Icon(Icons.error),
                        )),
                  ))
              .toList();

          /* imageSliders = sectionData
              .map((item) => InkWell(
                  onTap: () {
                    dev.log("Clicked item: $item");
                    channel.invokeMethod("onBannerClick", item);
                  },
                  child: CachedNetworkImage(
                    imageUrl: item["image"] as String,
                    imageBuilder: (context, imageProvider) => Container(
                      decoration: BoxDecoration(
                        image: DecorationImage(
                          image: imageProvider,
                          fit: BoxFit.fill,
                        ),
                      ),
                    ),
                    placeholder: (context, url) =>
                        Image.asset('assets/images/img.png'),
                    errorWidget: (context, url, error) =>
                        const Icon(Icons.error),
                  )))
              .toList();*/
          loading = false;
        });
      }
    });
    // Keep track of what the current platform lifecycle state is.
    WidgetsBinding.instance.addObserver(this);
    super.initState();
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    setState(() {
      appLifecycleState = state;
    });
  }

  // Show a random bright color.
  /* Color randomLightColor() {
    _random ??= Random(cellNumber);

    return Color.fromARGB(255, _random!.nextInt(50) + 205,
        _random!.nextInt(50) + 205, _random!.nextInt(50) + 205);
  }*/

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      // The Flutter cells will be noticeably different (due to background color
      // and the Flutter logo). The banner breaks immersion.
      debugShowCheckedModeBanner: true,
      home: Scaffold(
          backgroundColor: const Color(0xffffffff),
          body: loading
              ? const CircularProgressIndicator()
              : (imageSliders.length == 1
                  ? ClipRRect(
                      child: InkWell(
                          onTap: () {
                            dev.log("Clicked item: ${sectionData[0]}");
                            channel.invokeMethod(
                                "onBannerClick", sectionData[0]);
                          },
                          child: CachedNetworkImage(
                            imageUrl: sectionData[0]["image"] as String,
                            imageBuilder: (context, imageProvider) => Container(
                              decoration: BoxDecoration(
                                image: DecorationImage(
                                  image: imageProvider,
                                  fit: BoxFit.fill,
                                ),
                              ),
                            ),
                            placeholder: (context, url) =>
                                Image.asset('assets/images/img.png'),
                            errorWidget: (context, url, error) =>
                                const Icon(Icons.error),
                          )),
                    )
                  : Stack(
                      alignment: AlignmentDirectional.bottomCenter,
                      children: [
                          CarouselSlider(
                            items: imageSliders,
                            carouselController: _controller,
                            options: CarouselOptions(
                                height: 240,
                                autoPlay: true,
                                viewportFraction: 0.9999,
                                enlargeCenterPage: false,
                                autoPlayAnimationDuration: const Duration(
                                    milliseconds:
                                        PAGE_SCROLL_ANIMATION_DURATION),
                                autoPlayInterval:
                                    const Duration(seconds: SCROLL_DURATION),
                                pauseAutoPlayOnTouch: false,
                                onPageChanged: (index, reason) {
                                  setState(() {
                                    _current = index;
                                  });
                                }),
                          ),
                          Row(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: sectionData.asMap().entries.map((entry) {
                              return GestureDetector(
                                  onTap: () {
                                    if (sectionData.length > 1) {
                                      _controller.animateToPage(entry.key);
                                    }
                                  },
                                  child: Container(
                                    width: 6.0,
                                    height: 6.0,
                                    margin: const EdgeInsets.symmetric(
                                        vertical: 10.0, horizontal: 3.0),
                                    decoration: BoxDecoration(
                                      shape: BoxShape.circle,
                                      color: (_current == entry.key
                                          ? const Color(0xfff9c501)
                                          : const Color(0xffa49b8e).withOpacity(
                                              _current == entry.key
                                                  ? 1.0
                                                  : 1.0)),
                                    ),
                                  ));
                            }).toList(),
                          ),
                        ]))),
    );
  }

/*  @override
  Widget build(BuildContext context) {
    int initTime = DateTime.now().millisecondsSinceEpoch;
    Widget widget = MaterialApp(
      debugShowCheckedModeBanner: true,
      home: Material(
        color: const Color(0xffffffff),
        child: loading
            ? const CircularProgressIndicator()
            : Stack(alignment: AlignmentDirectional.bottomCenter, children: [
          PageView.builder(
            //controller: _pageController,
              scrollDirection: Axis.horizontal,
              itemCount: imageSliders.length,
              allowImplicitScrolling: true,
              onPageChanged: (page) {
                print("Called on page: $page");
                setState(() {
                  _currentPage = page;
                });
              },
              itemBuilder: (_, index) {
                return SizedBox(
                    width: 240.0,
                    height: 240.0,
                    child: imageSliders[index]);
              }),
          imageSliders.length > 1
              ? Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: sectionData.asMap().entries.map((entry) {
              return GestureDetector(
                  onTap: () {
                    if (sectionData.length > 1) {
                      _pageController.animateToPage(
                        _currentPage,
                        duration: const Duration(
                            milliseconds:
                            PAGE_SCROLL_ANIMATION_DURATION),
                        curve: Curves.easeIn,
                      );
                    }
                  },
                  child: Container(
                    width: 6.0,
                    height: 6.0,
                    margin: const EdgeInsets.symmetric(
                        vertical: 10.0, horizontal: 3.0),
                    decoration: BoxDecoration(
                      shape: BoxShape.circle,
                      color: (_currentPage == entry.key
                          ? const Color(0xfff9c501)
                          : const Color(0xffa49b8e).withOpacity(
                          _currentPage == entry.key
                              ? 1.0
                              : 1.0)),
                    ),
                  ));
            }).toList(),
          )
              : Container(),
        ]),
      ),
    );
    int timeDiff = DateTime.now().millisecondsSinceEpoch - initTime;
    dev.log("Took ${timeDiff}ms to build widget");
    return widget;
  }*/
}
