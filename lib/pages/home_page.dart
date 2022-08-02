import 'package:flutter/material.dart';
import 'package:tcp_socket_connection/tcp_socket_connection.dart';

class HomePage extends StatefulWidget {
  const HomePage({Key? key}) : super(key: key);

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  @override
  void initState() {
    super.initState();
    _demo();
  }

  Future<void> _demo() async {
    TcpSocketConnection socketConnection = TcpSocketConnection('127.0.0.1', 9876);
    socketConnection.connect(5000, "", (x) {
      print(x);
    });
    socketConnection.sendMessage('Hello World');
  }

  @override
  void dispose() {
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(
          'TCP Server',
        ),
      ),
    );
  }
}
