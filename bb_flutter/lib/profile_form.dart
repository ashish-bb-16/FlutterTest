import 'package:flutter/material.dart';

import 'routing.dart';

class Profile extends StatefulWidget {
  String usertype = "";
  Profile({required String usertype, Key? key}) : super(key: key){
    this.usertype = usertype;
  }

  @override
  State<Profile> createState() => _ProfileState();
}

class _ProfileState extends State<Profile> {

  final TextEditingController _namecontroller = TextEditingController();
  final TextEditingController _agecontroller = TextEditingController();

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: (){
        Navigator.of(context).pop();
        return Future.value(true);
      },
      child: Scaffold(
        appBar: AppBar(title: const Text("Profile Screen")),
        body: Center(
          child: Column(
            children: [
              Text("Profile Page : ${widget.usertype??""}"),
              TextField(controller: _namecontroller, decoration: const InputDecoration(
                labelText: "Name",
              ),),
              TextField(controller: _agecontroller, decoration: const InputDecoration(
                  labelText: "Age"
              ),),
              ElevatedButton(onPressed: _submitForm, child: const Text("Submit"))
            ],
          ),
        ),
      ),
    );
  }

  void _submitForm() {
    Map<String, dynamic> resultMap = {};
    resultMap['name'] = _namecontroller.text;
    resultMap['age'] = _agecontroller.text;
    Routing().NativeRoute("testimonial", resultMap);
  }
}
