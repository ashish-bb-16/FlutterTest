import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class SingleTestimonial extends StatefulWidget {
  String name = "", msg = "";

  SingleTestimonial({required String name, required String msg, Key? key})
      : super(key: key) {
    this.name = name;
    this.msg = msg;
  }

  @override
  State<SingleTestimonial> createState() => _SingleTestimonialState();
}

class _SingleTestimonialState extends State<SingleTestimonial> {
  @override
  Widget build(BuildContext context) {
    print("Pushed single testimonial screen");
    return WillPopScope(
      onWillPop: () {
        if (Navigator.canPop(context)) {
          print("popping something");
          Navigator.pop(context);
        } else {
          SystemNavigator.pop();
        }
        print("Popping single testimonial screen");
        return Future.value(true);
      },
      child: Scaffold(
        appBar: AppBar(title: const Text("Single Testimonial")),
        body: Center(
          child: Text("Name = ${widget.name}\n Age = ${widget.msg}"),
        ),
      ),
    );
  }
}
