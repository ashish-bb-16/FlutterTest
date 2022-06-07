import 'dart:convert';

import 'package:bb_flutter/my_navigator_observer.dart';
import 'package:bb_flutter/profile_form.dart';
import 'package:bb_flutter/settings.dart';
import 'package:flutter/services.dart';
import 'package:flutter/material.dart';

import 'single_testimonial.dart';

class Routing extends StatelessWidget {
  static const callback = MethodChannel('callback');
  BuildContext? mcontext;

  Routing({Key? key}) : super(key: key) {
    callback.setMethodCallHandler(_receiveFromNative);
  }

  NativeRoute(String route, Map<String, dynamic> args) {
    Map<String, dynamic> resultMap = {};
    resultMap['route'] = route;
    resultMap['args'] = args;

    callback.invokeMethod("fromFlutterToNative", json.encode(resultMap));
  }

  Future<void> _receiveFromNative(MethodCall call) async {
    try {
      print(call.method);

      if (call.method == "fromNativeToFlutter") {
        final String data = call.arguments.toString();
        final jData = jsonDecode(data);
        print("jData = $jData");
        String route = jData['route'];
        print("route = $route");
        final args = jsonDecode(jData['args'].toString());
        print("args = $args");
        //Navigator.of(mcontext!).pop();
        print("Default route : ${Navigator.defaultRouteName}");
        print("Navigator stack: ${Navigator.canPop(mcontext!)}");
        Navigator.of(mcontext!).pop();
        switch (route) {
          case 'profile_form':
            Navigator.push(
              mcontext!,
              MaterialPageRoute(
                builder: (context) => Profile(usertype: args['usertype']),
              ),
            );
            break;
          case 'single_testimonial':
            Navigator.push(
              mcontext!,
              MaterialPageRoute(
                builder: (context) => SingleTestimonial(
                  name: args['name'],
                  msg: args['msg'],
                ),
              ),
            );
            break;
          case 'settings':
            Navigator.push(
              mcontext!,
              MaterialPageRoute(
                builder: (context) => Settings(),
              ),
            );
            break;
          default:
            /*Navigator.push(
              mcontext!,
              MaterialPageRoute(
                builder: (context) => Profile(usertype: args['usertype']),
              ),
            );*/
            print('RouteLog: No route type found');
        }
      }
    } on PlatformException catch (e) {
      print(e);
      //platform may not able to send proper data.
    }
  }

  @override
  Widget build(BuildContext context) {
    mcontext = context;
    print("Pushed routing screen");
    return WillPopScope(
      onWillPop: () {
        if (Navigator.canPop(mcontext!)) {
          print("popping something");
          Navigator.pop(mcontext!);
        } else {
          SystemNavigator.pop();
        }
        print("Popping routing screen");
        return Future.value(true);
      },
      child: Container(),
    );
  }
}
