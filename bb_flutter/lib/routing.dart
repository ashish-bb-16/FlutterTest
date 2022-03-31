import 'dart:convert';

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
        Navigator.of(mcontext!).pop();
        switch (route) {
          case 'profile_form':
            Navigator.pushReplacement(
              mcontext!,
              MaterialPageRoute(
                builder: (context) => Profile(usertype: args['usertype']),
              ),
            );
            //Navigator.of(mcontext!).pop();
            break;
          case 'single_testimonial':
            Navigator.pushReplacement(
              mcontext!,
              MaterialPageRoute(
                builder: (context) => SingleTestimonial(name: args['name'], msg: args['msg'],),
              ),
            );
            //Navigator.of(mcontext!).pop();
            break;
          case 'settings':
            Navigator.pushReplacement(
              mcontext!,
              MaterialPageRoute(
                builder: (context) => Settings(),
              ),
            );
            //Navigator.of(mcontext!).pop();
            break;
          default:
            Navigator.pushReplacement(
              mcontext!,
              MaterialPageRoute(
                builder: (context) => Profile(usertype: args['usertype']),
              ),
            );
        }
      }
    } on PlatformException catch (e) {
      print(e);
      //platform may not able to send proper data.
    }
  }

  @override
  Widget build(BuildContext context) {
    print("build of Routing");
    mcontext = context;
    return Container();
  }
}
