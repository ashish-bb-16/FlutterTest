import 'package:flutter/material.dart';

class SingleTestimonial extends StatefulWidget {
  String name = "", msg = "";
  SingleTestimonial({required String name, required String msg, Key? key}) : super(key: key){
    this.name = name;
    this.msg = msg;
  }

  @override
  State<SingleTestimonial> createState() => _SingleTestimonialState();
}

class _SingleTestimonialState extends State<SingleTestimonial> {

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: (){
        Navigator.of(context).pop();
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