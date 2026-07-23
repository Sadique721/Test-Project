import 'dart:io';

import 'package:savbill/pages/model/file_detail.dart';
import 'package:cached_network_image/cached_network_image.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';

class FileGridItem extends StatelessWidget {
  FileDetail fileDetail;
  // List<FileDetail>? fileDetail;
  final Function()? onTapItem;
  Widget? bottomAction;

  FileGridItem(
      {Key? key, required this.fileDetail, this.onTapItem, this.bottomAction})
      : super(key: key);

  @override
  Widget build(BuildContext context) {


    return Stack(children: <Widget>[
      InkWell(
        onTap: onTapItem,
        child: fileDetail.fileType!.equalsIgnoreCase(Strings.pdf) ||
            fileDetail.fileType!.equalsIgnoreCase(Strings.xlsx) ||
            fileDetail.fileType!.equalsIgnoreCase(Strings.xls)
            ? Container(
                height: 68,
                width: 68,
                decoration: BoxDecoration(
                  border: Border.all(
                    color: fileDetail.fileType!.equalsIgnoreCase(Strings.pdf)
                        ? AppTheme.colorError
                        : AppTheme.colorPrimary,
                  ),
                  borderRadius: BorderRadius.circular(35.0),
                ),
                child: Center(
                  child: SvgPicture.asset(
                    fileDetail.fileType!.equalsIgnoreCase(Strings.pdf)
                        ? pdfSvg
                        : xlSvg,
                    color: fileDetail.fileType!.equalsIgnoreCase(Strings.pdf)
                        ? AppTheme.colorError
                        : AppTheme.colorPrimary,
                    width: Constant.CAMERA_ICON_SIZE,
                    height: Constant.CAMERA_ICON_SIZE,
                  ),
                ),
              )
            : Container(
                padding: const EdgeInsets.only(
                  bottom: 4.0,
                ),
                child: ((fileDetail.filePathLocal != null &&
                    fileDetail.filePathLocal!.isNotEmpty)
                    ? ClipOval(
                        child: Image.file(
                          File(fileDetail.filePathLocal!),
                          width: Constant.D_ROUNDED_IMAGE_W_H,
                          height: Constant.D_ROUNDED_IMAGE_W_H,
                          fit: BoxFit.cover,
                        ),
                      )
                    : ClipOval(
                        child: CachedNetworkImage(
                            width: Constant.D_ROUNDED_IMAGE_W_H,
                            height: Constant.D_ROUNDED_IMAGE_W_H,
                            placeholder: (context, url) =>
                                const CircularProgressIndicator(),
                            imageUrl: fileDetail.filePath!,
                            fit: BoxFit.cover),
                      )),
              ),
      ),
      Positioned(
        right: 0.0,
        bottom: 0.0,
        child: GestureDetector(
            onTap: () {

            },
            child: bottomAction ?? Container()
            ),
      ),
    ]);
  }
}
